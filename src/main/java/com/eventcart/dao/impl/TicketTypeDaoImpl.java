package com.eventcart.dao.impl;

import com.eventcart.dao.TicketTypeDao;
import com.eventcart.entity.TicketStatus;
import com.eventcart.entity.TicketType;
import com.eventcart.util.HibernateUtil;
import org.hibernate.Session;

import java.util.Collections;
import java.util.List;

/**
 * Hibernate implementation of TicketTypeDao.
 */
public class TicketTypeDaoImpl extends GenericDaoImpl<TicketType, Long> implements TicketTypeDao {

    public TicketTypeDaoImpl() {
        super(TicketType.class);
    }

    @Override
    public List<TicketType> findByEvent(Long eventId) {
        if (eventId == null) return Collections.emptyList();
        try (Session session = HibernateUtil.openSession()) {
            String hql = "FROM TicketType t WHERE t.event.id = :eventId ORDER BY t.price ASC";
            return session.createQuery(hql, TicketType.class)
                    .setParameter("eventId", eventId)
                    .getResultList();
        } catch (Exception e) {
            logger.error("Error finding ticket types for event id {}: {}", eventId, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<TicketType> findActiveByEvent(Long eventId) {
        if (eventId == null) return Collections.emptyList();
        try (Session session = HibernateUtil.openSession()) {
            String hql = "FROM TicketType t WHERE t.event.id = :eventId AND t.status = :status ORDER BY t.price ASC";
            return session.createQuery(hql, TicketType.class)
                    .setParameter("eventId", eventId)
                    .setParameter("status", TicketStatus.ACTIVE)
                    .getResultList();
        } catch (Exception e) {
            logger.error("Error finding active ticket types for event id {}: {}", eventId, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public boolean updateAvailableQuantity(Long ticketTypeId, int deltaQuantity) {
        if (ticketTypeId == null) return false;
        return HibernateUtil.executeInTransactionWithResult(session -> {
            TicketType ticket = session.get(TicketType.class, ticketTypeId);
            if (ticket == null) return false;

            int newQty = ticket.getAvailableQuantity() + deltaQuantity;
            if (newQty < 0 || newQty > ticket.getTotalQuantity()) {
                logger.warn("Invalid stock adjustment: current={}, delta={}, total={}",
                        ticket.getAvailableQuantity(), deltaQuantity, ticket.getTotalQuantity());
                return false;
            }

            ticket.setAvailableQuantity(newQty);
            session.merge(ticket);
            return true;
        });
    }

    @Override
    public java.util.Optional<TicketType> findByIdWithLock(Session session, Long ticketTypeId) {
        if (ticketTypeId == null || session == null) return java.util.Optional.empty();
        try {
            TicketType ticket = session.get(TicketType.class, ticketTypeId, org.hibernate.LockMode.PESSIMISTIC_WRITE);
            return java.util.Optional.ofNullable(ticket);
        } catch (Exception e) {
            logger.error("Error acquiring pessimistic write lock on ticket type {}: {}", ticketTypeId, e.getMessage(), e);
            throw e;
        }
    }
}
