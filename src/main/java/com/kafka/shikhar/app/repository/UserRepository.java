package com.kafka.shikhar.app.repository;

import com.kafka.shikhar.app.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for {@link UserEntity}.
 *
 * <p>Spring auto-generates the implementation at runtime; no manual impl needed.
 * Custom query methods follow the Spring Data naming convention so no JPQL is required.
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    /**
     * Find a user by their email address (used as login principal).
     *
     * @param email the email to look up
     * @return an {@link Optional} containing the matching entity, or empty if not found
     */
    Optional<UserEntity> findByEmail(String email);

    /**
     * Find a user by their public handle.
     *
     * @param username the username to look up
     * @return an {@link Optional} containing the matching entity, or empty if not found
     */
    Optional<UserEntity> findByUsername(String username);

    /**
     * Check whether a given email is already registered.
     *
     * @param email the email to check
     * @return {@code true} if a record exists for this email
     */
    boolean existsByEmail(String email);

    /**
     * Check whether a given username is already taken.
     *
     * @param username the username to check
     * @return {@code true} if a record exists for this username
     */
    boolean existsByUsername(String username);
}
