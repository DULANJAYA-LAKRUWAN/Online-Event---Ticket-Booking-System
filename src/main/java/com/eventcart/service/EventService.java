package com.eventcart.service;

import com.eventcart.entity.Event;
import com.eventcart.entity.EventStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for Event creation, updating, publishing, and searching.
 */
public interface EventService {

    Event createEvent(Long categoryId, Long organizerId, String title, String description,
                       String venue, String location, LocalDate eventDate, LocalTime eventTime,
                       String bannerImage, boolean featured, EventStatus status);

    Event updateEvent(Long eventId, Long categoryId, String title, String description,
                       String venue, String location, LocalDate eventDate, LocalTime eventTime,
                       String bannerImage, boolean featured, EventStatus status);

    void deleteEvent(Long eventId);

    Event updateStatus(Long eventId, EventStatus newStatus);

    Optional<Event> findById(Long id);

    Optional<Event> findBySlug(String slug);

    List<Event> findAll();

    List<Event> findPublishedEvents();

    List<Event> findFeaturedEvents(int limit);

    List<Event> findByCategory(Long categoryId);

    List<Event> searchEvents(String query, Long categoryId, String location, EventStatus status, int page, int pageSize);

    long countSearchEvents(String query, Long categoryId, String location, EventStatus status);

    String generateUniqueSlug(String title, Long currentId);
}
