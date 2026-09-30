package com.eventcart.service;

import com.eventcart.entity.TicketStatus;
import com.eventcart.entity.TicketType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for TicketType management and stock control.
 */
public interface TicketTypeService {

    TicketType createTicketType(Long eventId, String name, String description, BigDecimal price,
                                int totalQuantity, TicketStatus status);

    TicketType updateTicketType(Long ticketTypeId, String name, String description, BigDecimal price,
                                int totalQuantity, int availableQuantity, TicketStatus status);

    void deleteTicketType(Long ticketTypeId);

    TicketType toggleStatus(Long ticketTypeId);

    Optional<TicketType> findById(Long id);

    List<TicketType> findByEvent(Long eventId);

    List<TicketType> findActiveByEvent(Long eventId);

    boolean reserveStock(Long ticketTypeId, int quantity);

    boolean releaseStock(Long ticketTypeId, int quantity);
}
