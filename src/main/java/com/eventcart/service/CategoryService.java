package com.eventcart.service;

import com.eventcart.entity.Category;
import com.eventcart.entity.CategoryStatus;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for Category management and validation.
 */
public interface CategoryService {

    Category createCategory(String name, String description, String iconClass, CategoryStatus status);

    Category updateCategory(Long id, String name, String description, String iconClass, CategoryStatus status);

    void deleteCategory(Long id);

    Category toggleStatus(Long id);

    Optional<Category> findById(Long id);

    Optional<Category> findBySlug(String slug);

    List<Category> findAll();

    List<Category> findActiveCategories();

    String generateUniqueSlug(String name, Long currentId);
}
