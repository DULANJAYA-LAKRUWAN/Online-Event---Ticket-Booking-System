package com.eventcart.dao.impl;

import com.eventcart.dao.GenericDao;
import com.eventcart.util.HibernateUtil;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Generic Hibernate DAO implementation handling transactions and resource cleanup.
 */
public abstract class GenericDaoImpl<T, ID extends Serializable> implements GenericDao<T, ID> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private final Class<T> entityClass;

    protected GenericDaoImpl(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public T save(T entity) {
        return HibernateUtil.executeInTransactionWithResult(session -> {
            session.persist(entity);
            return entity;
        });
    }

    @Override
    public T update(T entity) {
        return HibernateUtil.executeInTransactionWithResult(session -> session.merge(entity));
    }

    @Override
    public void delete(T entity) {
        HibernateUtil.executeInTransaction(session -> {
            T merged = session.merge(entity);
            session.remove(merged);
        });
    }

    @Override
    public void deleteById(ID id) {
        HibernateUtil.executeInTransaction(session -> {
            T entity = session.get(entityClass, id);
            if (entity != null) {
                session.remove(entity);
            }
        });
    }

    @Override
    public Optional<T> findById(ID id) {
        try (Session session = HibernateUtil.openSession()) {
            T entity = session.get(entityClass, id);
            return Optional.ofNullable(entity);
        } catch (Exception e) {
            logger.error("Error finding {} by id {}: {}", entityClass.getSimpleName(), id, e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public List<T> findAll() {
        try (Session session = HibernateUtil.openSession()) {
            String hql = "FROM " + entityClass.getSimpleName();
            return session.createQuery(hql, entityClass).getResultList();
        } catch (Exception e) {
            logger.error("Error finding all {}: {}", entityClass.getSimpleName(), e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<T> findPaginated(int page, int pageSize) {
        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 10;
        int firstResult = (page - 1) * pageSize;

        try (Session session = HibernateUtil.openSession()) {
            String hql = "FROM " + entityClass.getSimpleName();
            return session.createQuery(hql, entityClass)
                    .setFirstResult(firstResult)
                    .setMaxResults(pageSize)
                    .getResultList();
        } catch (Exception e) {
            logger.error("Error finding paginated {}: {}", entityClass.getSimpleName(), e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public long count() {
        try (Session session = HibernateUtil.openSession()) {
            String hql = "SELECT count(e) FROM " + entityClass.getSimpleName() + " e";
            Long result = session.createQuery(hql, Long.class).getSingleResult();
            return result != null ? result : 0L;
        } catch (Exception e) {
            logger.error("Error counting {}: {}", entityClass.getSimpleName(), e.getMessage(), e);
            return 0L;
        }
    }
}
