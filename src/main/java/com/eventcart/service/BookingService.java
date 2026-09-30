package com.eventcart.service;

import com.eventcart.dto.CartDto;
import com.eventcart.entity.Booking;
import com.eventcart.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * Service managing transactional ticket reservation and checkout booking draft creation.
 */
public interface BookingService {

    /**
     * Executes atomic inventory reservation under database pessimistic write locking.
     * Decrements available quantities, creates booking and booking items in PENDING state.
     * Throws InventoryConflictException if any tier has insufficient inventory.
     */
    Booking createBookingDraft(User user, CartDto cart);

    /**
     * Finds a booking by its public reference code.
     */
    Optional<Booking> getBookingByReference(String bookingReference);

    /**
     * Retrieves all bookings placed by a specific user.
     */
    List<Booking> getBookingsByUser(Long userId);
}
