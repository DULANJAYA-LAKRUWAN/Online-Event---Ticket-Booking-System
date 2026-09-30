package com.eventcart.service;

import com.eventcart.dao.CategoryDao;
import com.eventcart.entity.Category;
import com.eventcart.entity.CategoryStatus;
import com.eventcart.exception.ValidationException;
import com.eventcart.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CategoryServiceTest {

    private MockCategoryDao mockCategoryDao;
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        mockCategoryDao = new MockCategoryDao();
        categoryService = new CategoryServiceImpl(mockCategoryDao);
    }

    @Test
    @DisplayName("Successfully creates category with valid inputs")
    void testCreateCategorySuccess() {
        Category cat = categoryService.createCategory("Tech & AI", "AI Conferences", "bi-cpu", CategoryStatus.ACTIVE);

        assertNotNull(cat);
        assertEquals("Tech & AI", cat.getName());
        assertEquals("tech-ai", cat.getSlug());
        assertEquals(CategoryStatus.ACTIVE, cat.getStatus());
        assertTrue(cat.isActive());
    }

    @Test
    @DisplayName("Rejects blank or invalid category name")
    void testCreateCategoryInvalidName() {
        assertThrows(ValidationException.class, () ->
                categoryService.createCategory("", "Description", "bi-tag", CategoryStatus.ACTIVE));
        assertThrows(ValidationException.class, () ->
                categoryService.createCategory("A", "Too short", "bi-tag", CategoryStatus.ACTIVE));
    }

    @Test
    @DisplayName("Rejects duplicate category name")
    void testDuplicateCategoryName() {
        categoryService.createCategory("Music", "Concerts", "bi-music", CategoryStatus.ACTIVE);
        assertThrows(ValidationException.class, () ->
                categoryService.createCategory("Music", "Another music", "bi-tag", CategoryStatus.ACTIVE));
    }

    @Test
    @DisplayName("Toggles category status between ACTIVE and INACTIVE")
    void testToggleCategoryStatus() {
        Category cat = categoryService.createCategory("Sports", "All sports", "bi-trophy", CategoryStatus.ACTIVE);
        assertEquals(CategoryStatus.ACTIVE, cat.getStatus());

        Category toggled = categoryService.toggleStatus(cat.getId());
        assertEquals(CategoryStatus.INACTIVE, toggled.getStatus());
        assertFalse(toggled.isActive());

        Category toggledBack = categoryService.toggleStatus(cat.getId());
        assertEquals(CategoryStatus.ACTIVE, toggledBack.getStatus());
        assertTrue(toggledBack.isActive());
    }

    // Lightweight In-Memory Mock DAO for Fast & Independent Unit Testing
    static class MockCategoryDao implements CategoryDao {
        private final java.util.Map<Long, Category> db = new java.util.HashMap<>();
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
        public java.util.List<Category> findActive() {
            return db.values().stream().filter(Category::isActive).toList();
        }

        @Override
        public java.util.List<Category> findAll() {
            return new java.util.ArrayList<>(db.values());
        }

        @Override
        public java.util.List<Category> findPaginated(int page, int pageSize) {
            return findAll();
        }

        @Override
        public long count() {
            return db.size();
        }
    }
}
