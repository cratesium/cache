package com.kafka.shikhar.app.service;

import com.kafka.shikhar.app.dto.response.UserResponse;

import java.util.List;

/**
 * User management service contract.
 *
 * <p>Provides CRUD-like operations on user accounts.
 * The caller receives safe {@link UserResponse} DTOs – never raw entities.
 */
public interface UserService {

    /**
     * Retrieve all registered users.
     *
     * @return a list of user response DTOs
     */
    List<UserResponse> getAllUsers();

    /**
     * Retrieve a single user by primary key.
     *
     * @param id the user's id
     * @return the user response DTO
     * @throws com.kafka.shikhar.app.exception.UserNotFoundException if not found
     */
    UserResponse getUserById(Long id);

    /**
     * Retrieve the currently authenticated user's profile.
     *
     * @param email the email extracted from the JWT principal
     * @return the user response DTO
     */
    UserResponse getProfile(String email);

    /**
     * Soft-delete (disable) a user account.
     *
     * @param id the user's id
     */
    void deleteUser(Long id);
}
