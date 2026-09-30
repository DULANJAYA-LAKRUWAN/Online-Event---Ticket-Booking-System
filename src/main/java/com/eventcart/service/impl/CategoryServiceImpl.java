package com.eventcart.service.impl;

import com.eventcart.dao.CategoryDao;
import com.eventcart.dao.impl.CategoryDaoImpl;
import com.eventcart.entity.Category;
import com.eventcart.entity.CategoryStatus;
import com.eventcart.exception.ResourceNotFoundException;
import com.eventcart.exception.ValidationException;
import com.eventcart.service.CategoryService;
import com.eventcart.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Service implementation for Category business logic and validation.
 */
public class CategoryServiceImpl implements CategoryService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);
    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    private final CategoryDao categoryDao;

    public CategoryServiceImpl() {
        this(new CategoryDaoImpl());
    }

    public CategoryServiceImpl(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    @Override
    public Category createCategory(String name, String description, String iconClass, CategoryStatus status) {
        logger.info("Creating new category: {}", name);

        if (!ValidationUtil.isNotBlank(name)) {
            throw new ValidationException("Category name is required.");
        }
        String cleanName = name.trim();
        if (cleanName.length() < 2 || cleanName.length() > 80) {
            throw new ValidationException("Category name must be between 2 and 80 characters.");
        }

        if (categoryDao.existsByName(cleanName)) {
            throw new ValidationException("A category with the name '" + cleanName + "' already exists.");
        }

        String slug = generateUniqueSlug(cleanName, null);
        Category category = new Category();
        category.setName(cleanName);
        category.setSlug(slug);
        category.setDescription(ValidationUtil.clean(description));
        category.setIconClass(ValidationUtil.isNotBlank(iconClass) ? iconClass.trim() : "bi-tag");
        category.setStatus(status != null ? status : CategoryStatus.ACTIVE);

        Category saved = categoryDao.save(category);
        logger.info("Category created successfully with ID: {}", saved.getId());
        return saved;
    }

    @Override
    public Category updateCategory(Long id, String name, String description, String iconClass, CategoryStatus status) {
        Category category = categoryDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));

        if (!ValidationUtil.isNotBlank(name)) {
            throw new ValidationException("Category name is required.");
        }
        String cleanName = name.trim();
        if (cleanName.length() < 2 || cleanName.length() > 80) {
            throw new ValidationException("Category name must be between 2 and 80 characters.");
        }

        // Check if another category has the same name
        Optional<Category> existing = categoryDao.findByName(cleanName);
        if (existing.isPresent() && !existing.get().getId().equals(id)) {
            throw new ValidationException("Another category with the name '" + cleanName + "' already exists.");
        }

        category.setName(cleanName);
        category.setSlug(generateUniqueSlug(cleanName, id));
        category.setDescription(ValidationUtil.clean(description));
        if (ValidationUtil.isNotBlank(iconClass)) {
            category.setIconClass(iconClass.trim());
        }
        if (status != null) {
            category.setStatus(status);
        }

        return categoryDao.update(category);
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = categoryDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));

        // Soft-deactivate if events are associated with it, or hard delete
        if (category.getEvents() != null && !category.getEvents().isEmpty()) {
            logger.info("Category has events linked. Setting status to INACTIVE instead of deleting.");
            category.setStatus(CategoryStatus.INACTIVE);
            categoryDao.update(category);
        } else {
            categoryDao.delete(category);
            logger.info("Category id {} deleted successfully.", id);
        }
    }

    @Override
    public Category toggleStatus(Long id) {
        Category category = categoryDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));

        CategoryStatus newStatus = (category.getStatus() == CategoryStatus.ACTIVE)
                ? CategoryStatus.INACTIVE
                : CategoryStatus.ACTIVE;

        category.setStatus(newStatus);
        Category updated = categoryDao.update(category);
        logger.info("Category id {} status toggled to {}", id, newStatus);
        return updated;
    }

    @Override
    public Optional<Category> findById(Long id) {
        return categoryDao.findById(id);
    }

    @Override
    public Optional<Category> findBySlug(String slug) {
        return categoryDao.findBySlug(slug);
    }

    @Override
    public List<Category> findAll() {
        return categoryDao.findAll();
    }

    @Override
    public List<Category> findActiveCategories() {
        return categoryDao.findActive();
    }

    @Override
    public String generateUniqueSlug(String name, Long currentId) {
        String base = toSlug(name);
        String candidate = base;
        int counter = 1;

        while (true) {
            Optional<Category> found = categoryDao.findBySlug(candidate);
            if (found.isEmpty() || (currentId != null && found.get().getId().equals(currentId))) {
                return candidate;
            }
            candidate = base + "-" + (++counter);
        }
    }

    private static String toSlug(String input) {
        if (input == null) return "category";
        String noWhiteSpace = WHITESPACE.matcher(input.trim()).replaceAll("-");
        String normalized = Normalizer.normalize(noWhiteSpace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        slug = slug.toLowerCase(Locale.ENGLISH).replaceAll("-{2,}", "-").replaceAll("^-|-$", "");
        return slug.isEmpty() ? "category" : slug;
    }
}
