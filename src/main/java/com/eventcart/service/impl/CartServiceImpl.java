package com.eventcart.service.impl;

import com.eventcart.dao.TicketTypeDao;
import com.eventcart.dao.impl.TicketTypeDaoImpl;
import com.eventcart.dto.CartDto;
import com.eventcart.dto.CartItemDto;
import com.eventcart.dto.CartValidationResult;
import com.eventcart.entity.Event;
import com.eventcart.entity.EventStatus;
import com.eventcart.entity.TicketStatus;
import com.eventcart.entity.TicketType;
import com.eventcart.exception.InventoryConflictException;
import com.eventcart.exception.ResourceNotFoundException;
import com.eventcart.exception.ValidationException;
import com.eventcart.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Business implementation of CartService enforcing session cart integrity and inventory rules.
 */
public class CartServiceImpl implements CartService {

    private static final Logger logger = LoggerFactory.getLogger(CartServiceImpl.class);

    private final TicketTypeDao ticketTypeDao;

    public CartServiceImpl() {
        this.ticketTypeDao = new TicketTypeDaoImpl();
    }

    public CartServiceImpl(TicketTypeDao ticketTypeDao) {
        this.ticketTypeDao = ticketTypeDao;
    }

    @Override
    public CartItemDto addItem(HttpSession session, Long ticketTypeId, int quantity) {
        if (session == null) {
            throw new ValidationException("Session has expired or is invalid.");
        }
        if (ticketTypeId == null) {
            throw new ValidationException("Ticket type identifier is required.");
        }
        if (quantity < 1) {
            throw new ValidationException("Quantity must be at least 1.");
        }

        TicketType ticket = ticketTypeDao.findById(ticketTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket type with ID " + ticketTypeId + " not found."));

        Event event = ticket.getEvent();
        if (event == null || event.getStatus() != EventStatus.PUBLISHED) {
            throw new ValidationException("Tickets can only be purchased for published events.");
        }

        if (ticket.getStatus() != TicketStatus.ACTIVE) {
            throw new ValidationException("This ticket category is currently inactive.");
        }

        if (ticket.getAvailableQuantity() <= 0) {
            throw new InventoryConflictException("Ticket '" + ticket.getName() + "' is currently sold out.");
        }

        CartDto cart = getCart(session);
        CartItemDto existingItem = cart.getItem(ticketTypeId);
        int currentQtyInCart = (existingItem != null) ? existingItem.getQuantity() : 0;
        int targetQuantity = currentQtyInCart + quantity;

        if (targetQuantity > CartDto.MAX_QUANTITY_PER_LINE) {
            throw new ValidationException("You cannot purchase more than " + CartDto.MAX_QUANTITY_PER_LINE + " tickets of this type per booking.");
        }

        if (targetQuantity > ticket.getAvailableQuantity()) {
            int remainingCanAdd = ticket.getAvailableQuantity() - currentQtyInCart;
            String msg = (remainingCanAdd > 0)
                    ? "Only " + ticket.getAvailableQuantity() + " tickets are available (" + remainingCanAdd + " more can be added to your cart)."
                    : "Only " + ticket.getAvailableQuantity() + " tickets are available and they are already in your cart.";
            throw new InventoryConflictException(msg);
        }

        CartItemDto itemDto = new CartItemDto(
                ticket.getId(),
                event.getId(),
                event.getTitle(),
                ticket.getName(),
                ticket.getPrice(), // Server price from DB
                quantity,
                ticket.getAvailableQuantity(),
                event.getBannerImage(),
                event.getEventDate(),
                event.getEventTime(),
                event.getVenue()
        );

        cart.addItem(itemDto);
        session.setAttribute(CartDto.SESSION_KEY, cart);

        logger.info("Added {}x '{}' (ID: {}) to cart. New item total: {}",
                quantity, ticket.getName(), ticketTypeId, cart.getItem(ticketTypeId).getQuantity());

        return cart.getItem(ticketTypeId);
    }

    @Override
    public CartItemDto updateItemQuantity(HttpSession session, Long ticketTypeId, int quantity) {
        if (session == null) {
            throw new ValidationException("Session has expired or is invalid.");
        }
        if (ticketTypeId == null) {
            throw new ValidationException("Ticket type identifier is required.");
        }

        CartDto cart = getCart(session);
        CartItemDto existing = cart.getItem(ticketTypeId);
        if (existing == null) {
            throw new ResourceNotFoundException("Item not found in current cart.");
        }

        if (quantity <= 0) {
            cart.removeItem(ticketTypeId);
            session.setAttribute(CartDto.SESSION_KEY, cart);
            return null;
        }

        if (quantity > CartDto.MAX_QUANTITY_PER_LINE) {
            throw new ValidationException("Maximum limit of " + CartDto.MAX_QUANTITY_PER_LINE + " tickets per tier reached.");
        }

        TicketType ticket = ticketTypeDao.findById(ticketTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket type no longer available."));

        if (ticket.getStatus() != TicketStatus.ACTIVE) {
            cart.removeItem(ticketTypeId);
            throw new ValidationException("This ticket type is no longer active and has been removed from your cart.");
        }

        if (quantity > ticket.getAvailableQuantity()) {
            throw new InventoryConflictException("Only " + ticket.getAvailableQuantity() + " tickets remain available.");
        }

        // Keep price and available stock fresh
        existing.setUnitPrice(ticket.getPrice());
        existing.setAvailableQuantity(ticket.getAvailableQuantity());
        cart.updateQuantity(ticketTypeId, quantity);
        session.setAttribute(CartDto.SESSION_KEY, cart);

        logger.info("Updated ticket ID {} quantity to {} in cart.", ticketTypeId, quantity);
        return existing;
    }

    @Override
    public boolean removeItem(HttpSession session, Long ticketTypeId) {
        if (session == null || ticketTypeId == null) return false;
        CartDto cart = getCart(session);
        boolean removed = cart.removeItem(ticketTypeId);
        session.setAttribute(CartDto.SESSION_KEY, cart);
        return removed;
    }

    @Override
    public void clearCart(HttpSession session) {
        if (session != null) {
            CartDto cart = getCart(session);
            cart.clear();
            session.setAttribute(CartDto.SESSION_KEY, cart);
        }
    }

    @Override
    public CartDto getCart(HttpSession session) {
        if (session == null) {
            return new CartDto();
        }
        CartDto cart = (CartDto) session.getAttribute(CartDto.SESSION_KEY);
        if (cart == null) {
            cart = new CartDto();
            session.setAttribute(CartDto.SESSION_KEY, cart);
        }
        return cart;
    }

    @Override
    public int getCartCount(HttpSession session) {
        return getCart(session).getTotalQuantity();
    }

    @Override
    public CartValidationResult validateCart(CartDto cart) {
        CartValidationResult result = new CartValidationResult();
        if (cart == null || cart.isEmpty()) {
            result.addError("Your shopping cart is empty.");
            return result;
        }

        for (CartItemDto item : cart.getItems()) {
            Optional<TicketType> optTicket = ticketTypeDao.findById(item.getTicketTypeId());
            if (optTicket.isEmpty()) {
                result.addError("Ticket tier '" + item.getTicketTypeName() + "' no longer exists.");
                continue;
            }

            TicketType ticket = optTicket.get();
            Event event = ticket.getEvent();

            if (event == null || event.getStatus() != EventStatus.PUBLISHED) {
                result.addError("Event '" + item.getEventTitle() + "' is no longer published for sales.");
                continue;
            }

            if (ticket.getStatus() != TicketStatus.ACTIVE) {
                result.addError("Ticket tier '" + ticket.getName() + "' has been deactivated.");
                continue;
            }

            if (item.getQuantity() > ticket.getAvailableQuantity()) {
                result.addError("Only " + ticket.getAvailableQuantity() + " tickets available for '" +
                        ticket.getName() + "' (you requested " + item.getQuantity() + ").");
            }

            // Price change detection
            if (ticket.getPrice().compareTo(item.getUnitPrice()) != 0) {
                result.addWarning("Price for '" + ticket.getName() + "' updated from LKR " +
                        item.getUnitPrice() + " to LKR " + ticket.getPrice() + ".");
                item.setUnitPrice(ticket.getPrice());
            }
            item.setAvailableQuantity(ticket.getAvailableQuantity());
        }

        return result;
    }
}
