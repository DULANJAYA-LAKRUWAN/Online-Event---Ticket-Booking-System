package com.eventcart.dao;

import com.eventcart.entity.Event;
import com.eventcart.entity.EventStatus;

import java.util.List;
import java.util.Optional;

/**
 * DAO interface for Event entity persistence.
 */
public interface EventDao extends GenericDao<Event, Long> {

    Optional<Event> findBySlug(String slug);

    boolean existsBySlug(String slug);

    List<Event> findPublished();

    List<Event> findFeatured(int limit);

    List<Event> findByCategory(Long categoryId);

    List<Event> findByOrganizer(Long organizerId);

    List<Event> searchEvents(String query, Long categoryId, String location, EventStatus status, int page, int pageSize);

    long countSearchEvents(String query, Long categoryId, String location, EventStatus status);
}
