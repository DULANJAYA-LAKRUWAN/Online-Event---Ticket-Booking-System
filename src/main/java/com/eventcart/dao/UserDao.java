package com.eventcart.dao;

import com.eventcart.entity.Role;
import com.eventcart.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * DAO interface for User persistence operations.
 */
public interface UserDao extends GenericDao<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByRole(Role role);
}
