package com.eventcart.service.impl;

import com.eventcart.dao.BookingDao;
import com.eventcart.dao.TicketTypeDao;
import com.eventcart.dao.impl.BookingDaoImpl;
import com.eventcart.dao.impl.TicketTypeDaoImpl;
import com.eventcart.dto.CartDto;
import com.eventcart.dto.CartItemDto;
import com.eventcart.entity.*;
import com.eventcart.exception.InventoryConflictException;
import com.eventcart.exception.ValidationException;
import com.eventcart.service.BookingService;
import com.eventcart.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of BookingService providing atomic inventory reservation and booking creation.
 */
public class BookingServiceImpl implements BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingServiceImpl.class);

    private final BookingDao bookingDao;
    private final TicketTypeDao ticketTypeDao;

    public BookingServiceImpl() {
        this.bookingDao = new BookingDaoImpl();
        this.ticketTypeDao = new TicketTypeDaoImpl();
    }

    public BookingServiceImpl(BookingDao bookingDao, TicketTypeDao ticketTypeDao) {
        this.bookingDao = bookingDao;
        this.ticketTypeDao = ticketTypeDao;
    }

    @Override
    public Booking createBookingDraft(User user, CartDto cart) {
        if (user == null || user.getId() == null) {
            throw new ValidationException("Authenticated user is required to complete checkout.");
        }
        if (cart == null || cart.isEmpty()) {
            throw new ValidationException("Cannot checkout with an empty shopping cart.");
        }

        Session session = null;
        Transaction tx = null;

        try {
            session = HibernateUtil.openSession();
            tx = session.beginTransaction();

            Booking booking = executeReservationTransaction(session, user, cart);

            tx.commit();
            logger.info("Successfully committed booking '{}' (ID: {}) for user '{}'. Total: LKR {}",
                    booking.getBookingReference(), booking.getId(), user.getEmail(), booking.getFinalAmount());
            return booking;

        } catch (InventoryConflictException | ValidationException e) {
            if (tx != null && tx.isActive()) {
                try {
                    tx.rollback();
                } catch (Exception rbEx) {
                    logger.error("Failed to rollback transaction: {}", rbEx.getMessage(), rbEx);
                }
            }
            throw e;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                try {
                    tx.rollback();
                } catch (Exception rbEx) {
                    logger.error("Failed to rollback transaction: {}", rbEx.getMessage(), rbEx);
                }
            }
            logger.error("Unexpected error during booking creation: {}", e.getMessage(), e);
            throw new RuntimeException("Could not complete ticket reservation: " + e.getMessage(), e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    /**
     * Executes atomic validation, pessimistic stock checks, and entity persistence.
     * Can be invoked directly in unit tests or within managed session.
     */
    public Booking executeReservationTransaction(Session session, User user, CartDto cart) {
        if (user == null || user.getId() == null) {
            throw new ValidationException("Authenticated user is required to complete checkout.");
        }
        if (cart == null || cart.isEmpty()) {
            throw new ValidationException("Cannot checkout with an empty shopping cart.");
        }

        // Re-fetch managed User in this session if session is provided
        User managedUser = (session != null) ? session.get(User.class, user.getId()) : user;
        if (managedUser == null) {
            managedUser = user;
        }
        if (!managedUser.isActive()) {
            throw new ValidationException("User account is inactive or not found.");
        }

        // Generate unique human-readable booking reference: EC-YYYYMMDD-XXXXXX
        String datePrefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomCode = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        String bookingRef = "EC-" + datePrefix + "-" + randomCode;

        Booking booking = new Booking();
        booking.setBookingReference(bookingRef);
        booking.setUser(managedUser);
        booking.setStatus(BookingStatus.PENDING);

        BigDecimal calculatedTotal = BigDecimal.ZERO;

        // Iterate over items and perform Pessimistic Row Locking (SELECT ... FOR UPDATE)
        for (CartItemDto cartItem : cart.getItems()) {
            Long ticketTypeId = cartItem.getTicketTypeId();
            int requestedQty = cartItem.getQuantity();

            if (requestedQty <= 0) {
                throw new ValidationException("Invalid ticket quantity: " + requestedQty);
            }

            Optional<TicketType> optTicket = ticketTypeDao.findByIdWithLock(session, ticketTypeId);
            if (optTicket.isEmpty()) {
                throw new InventoryConflictException("Ticket tier '" + cartItem.getTicketTypeName() + "' is no longer available.");
            }

            TicketType ticket = optTicket.get();
            Event event = ticket.getEvent();

            if (event == null || event.getStatus() != EventStatus.PUBLISHED) {
                throw new InventoryConflictException("Event '" + cartItem.getEventTitle() + "' is not open for ticket sales.");
            }

            if (ticket.getStatus() != TicketStatus.ACTIVE) {
                throw new InventoryConflictException("Ticket tier '" + ticket.getName() + "' is currently inactive.");
            }

            // Crucial concurrency check: prevent overselling under row lock
            if (ticket.getAvailableQuantity() < requestedQty) {
                logger.warn("Inventory conflict on ticket '{}' (ID: {}). Requested: {}, Available: {}",
                        ticket.getName(), ticketTypeId, requestedQty, ticket.getAvailableQuantity());
                throw new InventoryConflictException("Insufficient stock for ticket '" + ticket.getName() +
                        "'. Only " + ticket.getAvailableQuantity() + " tickets remain available.");
            }

            // Decrement inventory safely within transaction
            int newAvailableQty = ticket.getAvailableQuantity() - requestedQty;
            ticket.setAvailableQuantity(newAvailableQty);
            if (session != null) {
                session.merge(ticket);
            }

            // Compute fresh item subtotal
            BigDecimal itemPrice = ticket.getPrice();
            BigDecimal itemSubtotal = itemPrice.multiply(BigDecimal.valueOf(requestedQty));
            calculatedTotal = calculatedTotal.add(itemSubtotal);

            BookingItem bookingItem = new BookingItem(booking, ticket, requestedQty, itemPrice, itemSubtotal);
            booking.addItem(bookingItem);
        }

        booking.setTotalAmount(calculatedTotal);
        booking.setDiscountAmount(BigDecimal.ZERO);
        booking.setFinalAmount(calculatedTotal);

        if (session != null) {
            session.persist(booking);
        }

        return booking;
    }

    @Override
    public Optional<Booking> getBookingByReference(String bookingReference) {
        return bookingDao.findByReference(bookingReference);
    }

    @Override
    public List<Booking> getBookingsByUser(Long userId) {
        return bookingDao.findByUser(userId);
    }
}
