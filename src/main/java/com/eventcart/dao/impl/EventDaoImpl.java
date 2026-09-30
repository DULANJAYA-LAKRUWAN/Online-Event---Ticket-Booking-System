package com.eventcart.dao.impl;

import com.eventcart.dao.EventDao;
import com.eventcart.entity.CategoryStatus;
import com.eventcart.entity.Event;
import com.eventcart.entity.EventStatus;
import com.eventcart.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Hibernate implementation of EventDao with search, filtering, and pagination.
 */
public class EventDaoImpl extends GenericDaoImpl<Event, Long> implements EventDao {

    public EventDaoImpl() {
        super(Event.class);
    }

    @Override
    public Optional<Event> findById(Long id) {
        if (id == null) return Optional.empty();
        try (Session session = HibernateUtil.openSession()) {
            String hql = "SELECT DISTINCT e FROM Event e " +
                         "LEFT JOIN FETCH e.category " +
                         "LEFT JOIN FETCH e.ticketTypes " +
                         "WHERE e.id = :id";
            return session.createQuery(hql, Event.class)
                    .setParameter("id", id)
                    .uniqueResultOptional();
        } catch (Exception e) {
            logger.error("Error finding event by id {}: {}", id, e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Event> findBySlug(String slug) {
        if (slug == null) return Optional.empty();
        try (Session session = HibernateUtil.openSession()) {
            String hql = "SELECT DISTINCT e FROM Event e " +
                         "LEFT JOIN FETCH e.category " +
                         "LEFT JOIN FETCH e.ticketTypes " +
                         "WHERE LOWER(e.slug) = LOWER(:slug)";
            return session.createQuery(hql, Event.class)
                    .setParameter("slug", slug.trim())
                    .uniqueResultOptional();
        } catch (Exception e) {
            logger.error("Error finding event by slug '{}': {}", slug, e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public boolean existsBySlug(String slug) {
        if (slug == null) return false;
        try (Session session = HibernateUtil.openSession()) {
            String hql = "SELECT count(e.id) FROM Event e WHERE LOWER(e.slug) = LOWER(:slug)";
            Long count = session.createQuery(hql, Long.class)
                    .setParameter("slug", slug.trim())
                    .getSingleResult();
            return count != null && count > 0;
        } catch (Exception e) {
            logger.error("Error checking event existence by slug '{}': {}", slug, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public List<Event> findPublished() {
        try (Session session = HibernateUtil.openSession()) {
            String hql = "SELECT DISTINCT e FROM Event e " +
                         "JOIN FETCH e.category c " +
                         "LEFT JOIN FETCH e.ticketTypes " +
                         "WHERE e.status = :status AND c.status = :catStatus " +
                         "ORDER BY e.eventDate ASC, e.eventTime ASC";
            return session.createQuery(hql, Event.class)
                    .setParameter("status", EventStatus.PUBLISHED)
                    .setParameter("catStatus", CategoryStatus.ACTIVE)
                    .getResultList();
        } catch (Exception e) {
            logger.error("Error finding published events: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<Event> findFeatured(int limit) {
        try (Session session = HibernateUtil.openSession()) {
            String hql = "SELECT DISTINCT e FROM Event e " +
                         "JOIN FETCH e.category c " +
                         "LEFT JOIN FETCH e.ticketTypes " +
                         "WHERE e.status = :status AND e.featured = true AND c.status = :catStatus " +
                         "ORDER BY e.eventDate ASC";
            return session.createQuery(hql, Event.class)
                    .setParameter("status", EventStatus.PUBLISHED)
                    .setParameter("catStatus", CategoryStatus.ACTIVE)
                    .setMaxResults(limit > 0 ? limit : 6)
                    .getResultList();
        } catch (Exception e) {
            logger.error("Error finding featured events: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<Event> findByCategory(Long categoryId) {
        if (categoryId == null) return Collections.emptyList();
        try (Session session = HibernateUtil.openSession()) {
            String hql = "SELECT DISTINCT e FROM Event e " +
                         "JOIN FETCH e.category c " +
                         "LEFT JOIN FETCH e.ticketTypes " +
                         "WHERE c.id = :catId AND e.status = :status " +
                         "ORDER BY e.eventDate ASC";
            return session.createQuery(hql, Event.class)
                    .setParameter("catId", categoryId)
                    .setParameter("status", EventStatus.PUBLISHED)
                    .getResultList();
        } catch (Exception e) {
            logger.error("Error finding events by category id {}: {}", categoryId, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<Event> findByOrganizer(Long organizerId) {
        if (organizerId == null) return Collections.emptyList();
        try (Session session = HibernateUtil.openSession()) {
            String hql = "SELECT DISTINCT e FROM Event e " +
                         "JOIN FETCH e.category " +
                         "LEFT JOIN FETCH e.ticketTypes " +
                         "WHERE e.organizer.id = :orgId " +
                         "ORDER BY e.createdAt DESC";
            return session.createQuery(hql, Event.class)
                    .setParameter("orgId", organizerId)
                    .getResultList();
        } catch (Exception e) {
            logger.error("Error finding events by organizer id {}: {}", organizerId, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<Event> searchEvents(String query, Long categoryId, String location, EventStatus status, int page, int pageSize) {
        try (Session session = HibernateUtil.openSession()) {
            StringBuilder hql = new StringBuilder("SELECT DISTINCT e FROM Event e JOIN FETCH e.category c LEFT JOIN FETCH e.ticketTypes WHERE 1=1 ");

            if (status != null) {
                hql.append("AND e.status = :status ");
            }
            if (categoryId != null && categoryId > 0) {
                hql.append("AND c.id = :categoryId ");
            }
            if (query != null && !query.trim().isEmpty()) {
                hql.append("AND (LOWER(e.title) LIKE :query OR LOWER(e.description) LIKE :query OR LOWER(e.venue) LIKE :query) ");
            }
            if (location != null && !location.trim().isEmpty()) {
                hql.append("AND (LOWER(e.location) LIKE :loc OR LOWER(e.venue) LIKE :loc) ");
            }

            // Only show active categories for customer-facing queries
            if (status == EventStatus.PUBLISHED) {
                hql.append("AND c.status = :activeCatStatus ");
            }

            hql.append("ORDER BY e.eventDate ASC, e.eventTime ASC");

            Query<Event> q = session.createQuery(hql.toString(), Event.class);

            if (status != null) {
                q.setParameter("status", status);
            }
            if (categoryId != null && categoryId > 0) {
                q.setParameter("categoryId", categoryId);
            }
            if (query != null && !query.trim().isEmpty()) {
                q.setParameter("query", "%" + query.trim().toLowerCase() + "%");
            }
            if (location != null && !location.trim().isEmpty()) {
                q.setParameter("loc", "%" + location.trim().toLowerCase() + "%");
            }
            if (status == EventStatus.PUBLISHED) {
                q.setParameter("activeCatStatus", CategoryStatus.ACTIVE);
            }

            int firstResult = Math.max(0, (page - 1) * pageSize);
            q.setFirstResult(firstResult);
            q.setMaxResults(pageSize > 0 ? pageSize : 9);

            return q.getResultList();
        } catch (Exception e) {
            logger.error("Error searching events: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public long countSearchEvents(String query, Long categoryId, String location, EventStatus status) {
        try (Session session = HibernateUtil.openSession()) {
            StringBuilder hql = new StringBuilder("SELECT count(DISTINCT e.id) FROM Event e JOIN e.category c WHERE 1=1 ");

            if (status != null) {
                hql.append("AND e.status = :status ");
            }
            if (categoryId != null && categoryId > 0) {
                hql.append("AND c.id = :categoryId ");
            }
            if (query != null && !query.trim().isEmpty()) {
                hql.append("AND (LOWER(e.title) LIKE :query OR LOWER(e.description) LIKE :query OR LOWER(e.venue) LIKE :query) ");
            }
            if (location != null && !location.trim().isEmpty()) {
                hql.append("AND (LOWER(e.location) LIKE :loc OR LOWER(e.venue) LIKE :loc) ");
            }
            if (status == EventStatus.PUBLISHED) {
                hql.append("AND c.status = :activeCatStatus ");
            }

            Query<Long> q = session.createQuery(hql.toString(), Long.class);

            if (status != null) {
                q.setParameter("status", status);
            }
            if (categoryId != null && categoryId > 0) {
                q.setParameter("categoryId", categoryId);
            }
            if (query != null && !query.trim().isEmpty()) {
                q.setParameter("query", "%" + query.trim().toLowerCase() + "%");
            }
            if (location != null && !location.trim().isEmpty()) {
                q.setParameter("loc", "%" + location.trim().toLowerCase() + "%");
            }
            if (status == EventStatus.PUBLISHED) {
                q.setParameter("activeCatStatus", CategoryStatus.ACTIVE);
            }

            Long count = q.getSingleResult();
            return count != null ? count : 0L;
        } catch (Exception e) {
            logger.error("Error counting search events: {}", e.getMessage(), e);
            return 0L;
        }
    }
}
