package com.eventcart.service.impl;

import com.eventcart.dao.EventDao;
import com.eventcart.dao.TicketTypeDao;
import com.eventcart.dao.impl.EventDaoImpl;
import com.eventcart.dao.impl.TicketTypeDaoImpl;
import com.eventcart.entity.Event;
import com.eventcart.entity.TicketStatus;
import com.eventcart.entity.TicketType;
import com.eventcart.exception.ResourceNotFoundException;
import com.eventcart.exception.ValidationException;
import com.eventcart.service.TicketTypeService;
import com.eventcart.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service implementation for TicketType validation and inventory control.
 */
public class TicketTypeServiceImpl implements TicketTypeService {

    private static final Logger logger = LoggerFactory.getLogger(TicketTypeServiceImpl.class);

    private final TicketTypeDao ticketTypeDao;
    private final EventDao eventDao;

    public TicketTypeServiceImpl() {
        this(new TicketTypeDaoImpl(), new EventDaoImpl());
    }

    public TicketTypeServiceImpl(TicketTypeDao ticketTypeDao, EventDao eventDao) {
        this.ticketTypeDao = ticketTypeDao;
        this.eventDao = eventDao;
    }

    @Override
    public TicketType createTicketType(Long eventId, String name, String description, BigDecimal price,
                                        int totalQuantity, TicketStatus status) {
        logger.info("Creating ticket type '{}' for event id {}", name, eventId);

        Event event = eventDao.findById(eventId)
                .orElseThrow(() -> new ValidationException("Event does not exist."));

        validateTicketDetails(name, price, totalQuantity);

        TicketType ticket = new TicketType();
        ticket.setEvent(event);
        ticket.setName(name.trim());
        ticket.setDescription(ValidationUtil.clean(description));
        ticket.setPrice(price);
        ticket.setTotalQuantity(totalQuantity);
        ticket.setAvailableQuantity(totalQuantity);
        ticket.setStatus(status != null ? status : TicketStatus.ACTIVE);

        TicketType saved = ticketTypeDao.save(ticket);
        logger.info("TicketType created successfully with ID: {}", saved.getId());
        return saved;
    }

    @Override
    public TicketType updateTicketType(Long ticketTypeId, String name, String description, BigDecimal price,
                                        int totalQuantity, int availableQuantity, TicketStatus status) {
        TicketType ticket = ticketTypeDao.findById(ticketTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("TicketType", ticketTypeId));

        validateTicketDetails(name, price, totalQuantity);

        if (availableQuantity < 0 || availableQuantity > totalQuantity) {
            throw new ValidationException("Available quantity must be between 0 and total quantity (" + totalQuantity + ").");
        }

        ticket.setName(name.trim());
        ticket.setDescription(ValidationUtil.clean(description));
        ticket.setPrice(price);
        ticket.setTotalQuantity(totalQuantity);
        ticket.setAvailableQuantity(availableQuantity);
        if (status != null) {
            ticket.setStatus(status);
        }

        return ticketTypeDao.update(ticket);
    }

    @Override
    public void deleteTicketType(Long ticketTypeId) {
        TicketType ticket = ticketTypeDao.findById(ticketTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("TicketType", ticketTypeId));

        // Soft deactivate to avoid breaking booking history
        ticket.setStatus(TicketStatus.INACTIVE);
        ticketTypeDao.update(ticket);
        logger.info("TicketType id {} status set to INACTIVE.", ticketTypeId);
    }

    @Override
    public TicketType toggleStatus(Long ticketTypeId) {
        TicketType ticket = ticketTypeDao.findById(ticketTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("TicketType", ticketTypeId));

        TicketStatus newStatus = (ticket.getStatus() == TicketStatus.ACTIVE)
                ? TicketStatus.INACTIVE
                : TicketStatus.ACTIVE;

        ticket.setStatus(newStatus);
        TicketType updated = ticketTypeDao.update(ticket);
        logger.info("TicketType id {} toggled to {}", ticketTypeId, newStatus);
        return updated;
    }

    @Override
    public Optional<TicketType> findById(Long id) {
        return ticketTypeDao.findById(id);
    }

    @Override
    public List<TicketType> findByEvent(Long eventId) {
        return ticketTypeDao.findByEvent(eventId);
    }

    @Override
    public List<TicketType> findActiveByEvent(Long eventId) {
        return ticketTypeDao.findActiveByEvent(eventId);
    }

    @Override
    public boolean reserveStock(Long ticketTypeId, int quantity) {
        if (quantity <= 0) return false;
        return ticketTypeDao.updateAvailableQuantity(ticketTypeId, -quantity);
    }

    @Override
    public boolean releaseStock(Long ticketTypeId, int quantity) {
        if (quantity <= 0) return false;
        return ticketTypeDao.updateAvailableQuantity(ticketTypeId, quantity);
    }

    private void validateTicketDetails(String name, BigDecimal price, int totalQuantity) {
        if (!ValidationUtil.isNotBlank(name) || name.trim().length() < 2 || name.trim().length() > 80) {
            throw new ValidationException("Ticket tier name must be between 2 and 80 characters.");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Ticket price cannot be negative.");
        }
        if (totalQuantity <= 0) {
            throw new ValidationException("Total ticket quantity must be greater than zero.");
        }
    }
}
