package com.eventcart.service;

import com.eventcart.dao.EventDao;
import com.eventcart.dao.TicketTypeDao;
import com.eventcart.entity.*;
import com.eventcart.exception.ValidationException;
import com.eventcart.service.impl.TicketTypeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class TicketTypeServiceTest {

    private MockTicketTypeDao mockTicketTypeDao;
    private MockEventDao mockEventDao;
    private TicketTypeService ticketTypeService;
    private Event sampleEvent;

    @BeforeEach
    void setUp() {
        mockTicketTypeDao = new MockTicketTypeDao();
        mockEventDao = new MockEventDao();
        ticketTypeService = new TicketTypeServiceImpl(mockTicketTypeDao, mockEventDao);

        Category cat = new Category("Music", "music", "Concerts", "bi-music", CategoryStatus.ACTIVE);
        cat.setId(1L);

        sampleEvent = new Event();
        sampleEvent.setId(10L);
        sampleEvent.setTitle("Rock Arena Tour");
        sampleEvent.setCategory(cat);
        sampleEvent.setVenue("Madison Hall");
        sampleEvent.setLocation("New York, NY");
        sampleEvent.setEventDate(LocalDate.of(2026, 10, 15));
        sampleEvent.setEventTime(LocalTime.of(19, 0));
        sampleEvent.setStatus(EventStatus.PUBLISHED);
        mockEventDao.save(sampleEvent);
    }

    @Test
    @DisplayName("Successfully creates ticket type with valid parameters")
    void testCreateTicketSuccess() {
        TicketType ticket = ticketTypeService.createTicketType(
                sampleEvent.getId(),
                "VIP Platinum",
                "Front row seat + backstage meet & greet",
                new BigDecimal("150.00"),
                100,
                TicketStatus.ACTIVE
        );

        assertNotNull(ticket);
        assertEquals("VIP Platinum", ticket.getName());
        assertEquals(new BigDecimal("150.00"), ticket.getPrice());
        assertEquals(100, ticket.getTotalQuantity());
        assertEquals(100, ticket.getAvailableQuantity());
        assertTrue(ticket.isAvailable());
    }

    @Test
    @DisplayName("Rejects negative pricing and zero quantity")
    void testPriceAndQuantityValidation() {
        assertThrows(ValidationException.class, () ->
                ticketTypeService.createTicketType(sampleEvent.getId(), "VIP", "Desc", new BigDecimal("-10.00"), 50, TicketStatus.ACTIVE));

        assertThrows(ValidationException.class, () ->
                ticketTypeService.createTicketType(sampleEvent.getId(), "VIP", "Desc", new BigDecimal("50.00"), 0, TicketStatus.ACTIVE));

        assertThrows(ValidationException.class, () ->
                ticketTypeService.createTicketType(sampleEvent.getId(), "", "Desc", new BigDecimal("50.00"), 50, TicketStatus.ACTIVE));
    }

    @Test
    @DisplayName("Handles stock reservation and release correctly")
    void testStockReservation() {
        TicketType ticket = ticketTypeService.createTicketType(
                sampleEvent.getId(), "General", "Standing area", new BigDecimal("45.00"), 50, TicketStatus.ACTIVE
        );

        assertTrue(ticketTypeService.reserveStock(ticket.getId(), 5));
        TicketType afterReserve = ticketTypeService.findById(ticket.getId()).orElseThrow();
        assertEquals(45, afterReserve.getAvailableQuantity());

        assertTrue(ticketTypeService.releaseStock(ticket.getId(), 5));
        TicketType afterRelease = ticketTypeService.findById(ticket.getId()).orElseThrow();
        assertEquals(50, afterRelease.getAvailableQuantity());
    }

    static class MockTicketTypeDao implements TicketTypeDao {
        private final Map<Long, TicketType> db = new HashMap<>();
        private long idGen = 1;

        @Override
        public TicketType save(TicketType entity) {
            entity.setId(idGen++);
            db.put(entity.getId(), entity);
            return entity;
        }

        @Override
        public TicketType update(TicketType entity) {
            db.put(entity.getId(), entity);
            return entity;
        }

        @Override
        public void delete(TicketType entity) {
            db.remove(entity.getId());
        }

        @Override
        public void deleteById(Long id) {
            db.remove(id);
        }

        @Override
        public Optional<TicketType> findById(Long id) {
            return Optional.ofNullable(db.get(id));
        }

        @Override
        public List<TicketType> findByEvent(Long eventId) {
            return db.values().stream().filter(t -> t.getEvent().getId().equals(eventId)).toList();
        }

        @Override
        public List<TicketType> findActiveByEvent(Long eventId) {
            return db.values().stream().filter(t -> t.getEvent().getId().equals(eventId) && t.getStatus() == TicketStatus.ACTIVE).toList();
        }

        @Override
        public boolean updateAvailableQuantity(Long ticketTypeId, int deltaQuantity) {
            TicketType t = db.get(ticketTypeId);
            if (t == null) return false;
            int newQty = t.getAvailableQuantity() + deltaQuantity;
            if (newQty < 0 || newQty > t.getTotalQuantity()) return false;
            t.setAvailableQuantity(newQty);
            return true;
        }

        @Override public List<TicketType> findAll() { return new ArrayList<>(db.values()); }
        @Override public List<TicketType> findPaginated(int page, int pageSize) { return findAll(); }
        @Override public long count() { return db.size(); }
        @Override public Optional<TicketType> findByIdWithLock(org.hibernate.Session session, Long ticketTypeId) { return findById(ticketTypeId); }
    }

    static class MockEventDao implements EventDao {
        private final Map<Long, Event> db = new HashMap<>();

        @Override public Event save(Event entity) { db.put(entity.getId(), entity); return entity; }
        @Override public Event update(Event entity) { db.put(entity.getId(), entity); return entity; }
        @Override public void delete(Event entity) { db.remove(entity.getId()); }
        @Override public void deleteById(Long id) { db.remove(id); }
        @Override public Optional<Event> findById(Long id) { return Optional.ofNullable(db.get(id)); }
        @Override public Optional<Event> findBySlug(String slug) { return Optional.empty(); }
        @Override public boolean existsBySlug(String slug) { return false; }
        @Override public List<Event> findPublished() { return Collections.emptyList(); }
        @Override public List<Event> findFeatured(int limit) { return Collections.emptyList(); }
        @Override public List<Event> findByCategory(Long categoryId) { return Collections.emptyList(); }
        @Override public List<Event> findByOrganizer(Long organizerId) { return Collections.emptyList(); }
        @Override public List<Event> searchEvents(String query, Long categoryId, String location, EventStatus status, int page, int pageSize) { return Collections.emptyList(); }
        @Override public long countSearchEvents(String query, Long categoryId, String location, EventStatus status) { return 0; }
        @Override public List<Event> findAll() { return Collections.emptyList(); }
        @Override public List<Event> findPaginated(int page, int pageSize) { return Collections.emptyList(); }
        @Override public long count() { return db.size(); }
    }
}
