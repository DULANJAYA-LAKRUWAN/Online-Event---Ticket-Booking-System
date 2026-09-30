package com.eventcart.service;

import com.eventcart.dao.CategoryDao;
import com.eventcart.dao.EventDao;
import com.eventcart.dao.UserDao;
import com.eventcart.entity.*;
import com.eventcart.exception.ValidationException;
import com.eventcart.service.impl.EventServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class EventServiceTest {

    private MockEventDao mockEventDao;
    private MockCategoryDao mockCategoryDao;
    private MockUserDao mockUserDao;
    private EventService eventService;
    private Category activeCategory;
    private Category inactiveCategory;

    @BeforeEach
    void setUp() {
        mockEventDao = new MockEventDao();
        mockCategoryDao = new MockCategoryDao();
        mockUserDao = new MockUserDao();
        eventService = new EventServiceImpl(mockEventDao, mockCategoryDao, mockUserDao);

        activeCategory = new Category("Music", "music", "Live music", "bi-music", CategoryStatus.ACTIVE);
        mockCategoryDao.save(activeCategory);

        inactiveCategory = new Category("Archived", "archived", "Archived events", "bi-archive", CategoryStatus.INACTIVE);
        mockCategoryDao.save(inactiveCategory);
    }

    @Test
    @DisplayName("Successfully creates event with valid parameters and generated slug")
    void testCreateEventSuccess() {
        Event event = eventService.createEvent(
                activeCategory.getId(),
                null,
                "Summer Symphony 2026",
                "Grand outdoor symphony performance",
                "Central Park Arena",
                "New York, NY",
                LocalDate.of(2026, 8, 15),
                LocalTime.of(19, 30),
                "uploads/events/banner1.jpg",
                true,
                EventStatus.PUBLISHED
        );

        assertNotNull(event);
        assertEquals("Summer Symphony 2026", event.getTitle());
        assertEquals("summer-symphony-2026", event.getSlug());
        assertEquals(EventStatus.PUBLISHED, event.getStatus());
        assertTrue(event.isFeatured());
        assertEquals("Central Park Arena", event.getVenue());
    }

    @Test
    @DisplayName("Prevents publishing event under an inactive category")
    void testCannotPublishUnderInactiveCategory() {
        assertThrows(ValidationException.class, () ->
                eventService.createEvent(
                        inactiveCategory.getId(),
                        null,
                        "Archived Concert",
                        "Some description",
                        "Hall 1",
                        "Chicago, IL",
                        LocalDate.of(2026, 9, 10),
                        LocalTime.of(20, 0),
                        null,
                        false,
                        EventStatus.PUBLISHED
                )
        );
    }

    @Test
    @DisplayName("Validates mandatory event fields")
    void testEventFieldValidation() {
        // Missing title
        assertThrows(ValidationException.class, () ->
                eventService.createEvent(activeCategory.getId(), null, "", "Desc", "Venue", "Loc", LocalDate.now(), LocalTime.now(), null, false, EventStatus.DRAFT));

        // Missing venue
        assertThrows(ValidationException.class, () ->
                eventService.createEvent(activeCategory.getId(), null, "Concert", "Desc", "", "Loc", LocalDate.now(), LocalTime.now(), null, false, EventStatus.DRAFT));

        // Missing date
        assertThrows(ValidationException.class, () ->
                eventService.createEvent(activeCategory.getId(), null, "Concert", "Desc", "Venue", "Loc", null, LocalTime.now(), null, false, EventStatus.DRAFT));
    }

    @Test
    @DisplayName("Successfully updates event status")
    void testUpdateStatus() {
        Event event = eventService.createEvent(
                activeCategory.getId(), null, "Tech Summit", "AI conference", "Convention Hall",
                "Boston, MA", LocalDate.of(2026, 11, 20), LocalTime.of(9, 0), null, false, EventStatus.DRAFT
        );
        assertEquals(EventStatus.DRAFT, event.getStatus());

        Event published = eventService.updateStatus(event.getId(), EventStatus.PUBLISHED);
        assertEquals(EventStatus.PUBLISHED, published.getStatus());
        assertTrue(published.isPublished());
    }

    // Mock DAOs
    static class MockEventDao implements EventDao {
        private final Map<Long, Event> db = new HashMap<>();
        private long idGen = 1;

        @Override
        public Event save(Event entity) {
            entity.setId(idGen++);
            db.put(entity.getId(), entity);
            return entity;
        }

        @Override
        public Event update(Event entity) {
            db.put(entity.getId(), entity);
            return entity;
        }

        @Override
        public void delete(Event entity) {
            db.remove(entity.getId());
        }

        @Override
        public void deleteById(Long id) {
            db.remove(id);
        }

        @Override
        public Optional<Event> findById(Long id) {
            return Optional.ofNullable(db.get(id));
        }

        @Override
        public Optional<Event> findBySlug(String slug) {
            return db.values().stream().filter(e -> e.getSlug().equalsIgnoreCase(slug)).findFirst();
        }

        @Override
        public boolean existsBySlug(String slug) {
            return db.values().stream().anyMatch(e -> e.getSlug().equalsIgnoreCase(slug));
        }

        @Override
        public List<Event> findPublished() {
            return db.values().stream().filter(Event::isPublished).toList();
        }

        @Override
        public List<Event> findFeatured(int limit) {
            return db.values().stream().filter(e -> e.isPublished() && e.isFeatured()).limit(limit).toList();
        }

        @Override
        public List<Event> findByCategory(Long categoryId) {
            return db.values().stream().filter(e -> e.getCategory().getId().equals(categoryId)).toList();
        }

        @Override
        public List<Event> findByOrganizer(Long organizerId) {
            return db.values().stream().filter(e -> e.getOrganizer() != null && e.getOrganizer().getId().equals(organizerId)).toList();
        }

        @Override
        public List<Event> searchEvents(String query, Long categoryId, String location, EventStatus status, int page, int pageSize) {
            return db.values().stream().toList();
        }

        @Override
        public long countSearchEvents(String query, Long categoryId, String location, EventStatus status) {
            return db.size();
        }

        @Override
        public List<Event> findAll() {
            return new ArrayList<>(db.values());
        }

        @Override
        public List<Event> findPaginated(int page, int pageSize) {
            return findAll();
        }

        @Override
        public long count() {
            return db.size();
        }
    }

    static class MockCategoryDao implements CategoryDao {
        private final Map<Long, Category> db = new HashMap<>();
        private long idGen = 1;

        @Override
        public Category save(Category entity) {
            entity.setId(idGen++);
            db.put(entity.getId(), entity);
            return entity;
        }

        @Override
        public Category update(Category entity) {
            db.put(entity.getId(), entity);
            return entity;
        }

        @Override
        public void delete(Category entity) {
            db.remove(entity.getId());
        }

        @Override
        public void deleteById(Long id) {
            db.remove(id);
        }

        @Override
        public Optional<Category> findById(Long id) {
            return Optional.ofNullable(db.get(id));
        }

        @Override
        public Optional<Category> findBySlug(String slug) {
            return db.values().stream().filter(c -> c.getSlug().equalsIgnoreCase(slug)).findFirst();
        }

        @Override
        public Optional<Category> findByName(String name) {
            return db.values().stream().filter(c -> c.getName().equalsIgnoreCase(name)).findFirst();
        }

        @Override
        public boolean existsByName(String name) {
            return db.values().stream().anyMatch(c -> c.getName().equalsIgnoreCase(name));
        }

        @Override
        public boolean existsBySlug(String slug) {
            return db.values().stream().anyMatch(c -> c.getSlug().equalsIgnoreCase(slug));
        }

        @Override
        public List<Category> findActive() {
            return db.values().stream().filter(Category::isActive).toList();
        }

        @Override
        public List<Category> findAll() {
            return new ArrayList<>(db.values());
        }

        @Override
        public List<Category> findPaginated(int page, int pageSize) {
            return findAll();
        }

        @Override
        public long count() {
            return db.size();
        }
    }

    static class MockUserDao implements UserDao {
        @Override public Optional<User> findByEmail(String email) { return Optional.empty(); }
        @Override public boolean existsByEmail(String email) { return false; }
        @Override public List<User> findByRole(Role role) { return Collections.emptyList(); }
        @Override public User save(User entity) { return entity; }
        @Override public User update(User entity) { return entity; }
        @Override public void delete(User entity) {}
        @Override public void deleteById(Long id) {}
        @Override public Optional<User> findById(Long id) { return Optional.empty(); }
        @Override public List<User> findAll() { return Collections.emptyList(); }
        @Override public List<User> findPaginated(int page, int pageSize) { return Collections.emptyList(); }
        @Override public long count() { return 0; }
    }
}
