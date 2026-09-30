package com.eventcart.dao;

import com.eventcart.entity.Category;

import java.util.List;
import java.util.Optional;

/**
 * DAO interface for Category entity persistence.
 */
public interface CategoryDao extends GenericDao<Category, Long> {

    Optional<Category> findBySlug(String slug);

    Optional<Category> findByName(String name);

    boolean existsByName(String name);

    boolean existsBySlug(String slug);

    List<Category> findActive();
}
