package com.eventcart.dao;

import com.eventcart.entity.TicketType;

import java.util.List;

/**
 * DAO interface for TicketType persistence operations.
 */
public interface TicketTypeDao extends GenericDao<TicketType, Long> {

    List<TicketType> findByEvent(Long eventId);

    List<TicketType> findActiveByEvent(Long eventId);

    boolean updateAvailableQuantity(Long ticketTypeId, int deltaQuantity);
}
