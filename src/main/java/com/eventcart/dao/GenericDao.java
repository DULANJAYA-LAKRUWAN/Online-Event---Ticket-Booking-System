package com.eventcart.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * Generic DAO interface defining standard CRUD operations.
 *
 * @param <T>  Entity type
 * @param <ID> Primary key identifier type
 */
public interface GenericDao<T, ID extends Serializable> {

    T save(T entity);

    T update(T entity);

    void delete(T entity);

    void deleteById(ID id);

    Optional<T> findById(ID id);

    List<T> findAll();

    List<T> findPaginated(int page, int pageSize);

    long count();
}
