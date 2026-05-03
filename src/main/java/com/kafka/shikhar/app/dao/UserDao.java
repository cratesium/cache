package com.kafka.shikhar.app.dao;

import com.kafka.shikhar.app.entity.UserEntity;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object contract for user persistence operations.
 *
 * <p>The DAO layer sits between the service and the repository.
 * It abstracts the data source so the service does not depend directly
 * on Spring Data interfaces and can be tested in isolation.
 *
 * <p>All methods operate on the JPA {@link UserEntity} – mapping to/from
 * DTOs is the responsibility of the service layer.
 */
public interface UserDao {

    /**
     * Persist a new user entity.
     *
     * @param user the entity to save (must not be {@code null})
     * @return the saved entity with its generated {@code id}
     */
    UserEntity save(UserEntity user);

    /**
     * Retrieve all users from the data store.
     *
     * @return a list of all users; never {@code null}, may be empty
     */
    List<UserEntity> findAll();

    /**
     * Find a single user by primary key.
     *
     * @param id the user's primary key
     * @return an {@link Optional} containing the entity if found
     */
    Optional<UserEntity> findById(Long id);

    /**
     * Find a user by their email address.
     *
     * @param email the email to search by
     * @return an {@link Optional} containing the entity if found
     */
    Optional<UserEntity> findByEmail(String email);

    /**
     * Check whether an email is already registered.
     *
     * @param email the email to check
     * @return {@code true} if the email is already in use
     */
    boolean existsByEmail(String email);

    /**
     * Check whether a username is already taken.
     *
     * @param username the username to check
     * @return {@code true} if the username is already in use
     */
    boolean existsByUsername(String username);
}
