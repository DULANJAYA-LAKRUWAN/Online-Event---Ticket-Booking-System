package com.eventcart.dao.impl;

import com.eventcart.dao.UserDao;
import com.eventcart.entity.Role;
import com.eventcart.entity.User;
import com.eventcart.util.HibernateUtil;
import org.hibernate.Session;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Hibernate implementation of UserDao.
 */
public class UserDaoImpl extends GenericDaoImpl<User, Long> implements UserDao {

    public UserDaoImpl() {
        super(User.class);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        if (email == null) return Optional.empty();
        try (Session session = HibernateUtil.openSession()) {
            String hql = "FROM User u WHERE LOWER(u.email) = LOWER(:email)";
            return session.createQuery(hql, User.class)
                    .setParameter("email", email.trim())
                    .uniqueResultOptional();
        } catch (Exception e) {
            logger.error("Error finding user by email '{}': {}", email, e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        if (email == null) return false;
        try (Session session = HibernateUtil.openSession()) {
            String hql = "SELECT count(u.id) FROM User u WHERE LOWER(u.email) = LOWER(:email)";
            Long count = session.createQuery(hql, Long.class)
                    .setParameter("email", email.trim())
                    .getSingleResult();
            return count != null && count > 0;
        } catch (Exception e) {
            logger.error("Error checking existence for email '{}': {}", email, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public List<User> findByRole(Role role) {
        if (role == null) return Collections.emptyList();
        try (Session session = HibernateUtil.openSession()) {
            String hql = "FROM User u WHERE u.role = :role";
            return session.createQuery(hql, User.class)
                    .setParameter("role", role)
                    .getResultList();
        } catch (Exception e) {
            logger.error("Error finding users by role {}: {}", role, e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}
