package com.eventcart.dao;

import com.eventcart.entity.Booking;

import java.util.List;
import java.util.Optional;

/**
 * DAO interface for Booking persistence operations.
 */
public interface BookingDao extends GenericDao<Booking, Long> {

    Optional<Booking> findByReference(String bookingReference);

    List<Booking> findByUser(Long userId);
}
