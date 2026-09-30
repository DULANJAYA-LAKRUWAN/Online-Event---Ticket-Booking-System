package com.eventcart.dao.impl;

import com.eventcart.dao.BookingDao;
import com.eventcart.entity.Booking;
import com.eventcart.util.HibernateUtil;
import org.hibernate.Session;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Hibernate implementation of BookingDao.
 */
public class BookingDaoImpl extends GenericDaoImpl<Booking, Long> implements BookingDao {

    public BookingDaoImpl() {
        super(Booking.class);
    }

    @Override
    public Optional<Booking> findByReference(String bookingReference) {
        if (bookingReference == null || bookingReference.trim().isEmpty()) {
            return Optional.empty();
        }
        try (Session session = HibernateUtil.openSession()) {
            String hql = "SELECT DISTINCT b FROM Booking b LEFT JOIN FETCH b.items i LEFT JOIN FETCH i.ticketType t LEFT JOIN FETCH t.event WHERE b.bookingReference = :ref";
            Booking booking = session.createQuery(hql, Booking.class)
                    .setParameter("ref", bookingReference.trim())
                    .uniqueResult();
            return Optional.ofNullable(booking);
        } catch (Exception e) {
            logger.error("Error finding booking by reference {}: {}", bookingReference, e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public List<Booking> findByUser(Long userId) {
        if (userId == null) return Collections.emptyList();
        try (Session session = HibernateUtil.openSession()) {
            String hql = "SELECT DISTINCT b FROM Booking b LEFT JOIN FETCH b.items i LEFT JOIN FETCH i.ticketType t WHERE b.user.id = :userId ORDER BY b.createdAt DESC";
            return session.createQuery(hql, Booking.class)
                    .setParameter("userId", userId)
                    .getResultList();
        } catch (Exception e) {
            logger.error("Error finding bookings for user {}: {}", userId, e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}
