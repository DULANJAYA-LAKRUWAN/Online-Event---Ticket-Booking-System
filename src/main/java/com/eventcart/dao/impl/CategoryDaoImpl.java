package com.eventcart.dao.impl;

import com.eventcart.dao.CategoryDao;
import com.eventcart.entity.Category;
import com.eventcart.entity.CategoryStatus;
import com.eventcart.util.HibernateUtil;
import org.hibernate.Session;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Hibernate implementation of CategoryDao.
 */
public class CategoryDaoImpl extends GenericDaoImpl<Category, Long> implements CategoryDao {

    public CategoryDaoImpl() {
        super(Category.class);
    }

    @Override
    public Optional<Category> findBySlug(String slug) {
        if (slug == null) return Optional.empty();
        try (Session session = HibernateUtil.openSession()) {
            String hql = "FROM Category c WHERE LOWER(c.slug) = LOWER(:slug)";
            return session.createQuery(hql, Category.class)
                    .setParameter("slug", slug.trim())
                    .uniqueResultOptional();
        } catch (Exception e) {
            logger.error("Error finding category by slug '{}': {}", slug, e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Category> findByName(String name) {
        if (name == null) return Optional.empty();
        try (Session session = HibernateUtil.openSession()) {
            String hql = "FROM Category c WHERE LOWER(c.name) = LOWER(:name)";
            return session.createQuery(hql, Category.class)
                    .setParameter("name", name.trim())
                    .uniqueResultOptional();
        } catch (Exception e) {
            logger.error("Error finding category by name '{}': {}", name, e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public boolean existsByName(String name) {
        if (name == null) return false;
        try (Session session = HibernateUtil.openSession()) {
            String hql = "SELECT count(c.id) FROM Category c WHERE LOWER(c.name) = LOWER(:name)";
            Long count = session.createQuery(hql, Long.class)
                    .setParameter("name", name.trim())
                    .getSingleResult();
            return count != null && count > 0;
        } catch (Exception e) {
            logger.error("Error checking category existence by name '{}': {}", name, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean existsBySlug(String slug) {
        if (slug == null) return false;
        try (Session session = HibernateUtil.openSession()) {
            String hql = "SELECT count(c.id) FROM Category c WHERE LOWER(c.slug) = LOWER(:slug)";
            Long count = session.createQuery(hql, Long.class)
                    .setParameter("slug", slug.trim())
                    .getSingleResult();
            return count != null && count > 0;
        } catch (Exception e) {
            logger.error("Error checking category existence by slug '{}': {}", slug, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public List<Category> findActive() {
        try (Session session = HibernateUtil.openSession()) {
            String hql = "FROM Category c WHERE c.status = :status ORDER BY c.name ASC";
            return session.createQuery(hql, Category.class)
                    .setParameter("status", CategoryStatus.ACTIVE)
                    .getResultList();
        } catch (Exception e) {
            logger.error("Error finding active categories: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}
