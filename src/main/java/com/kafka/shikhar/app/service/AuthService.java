package com.kafka.shikhar.app.service;

import com.kafka.shikhar.app.dto.request.RegisterRequest;
import com.kafka.shikhar.app.dto.response.AuthResponse;
import com.kafka.shikhar.app.dto.request.LoginRequest;

/**
 * Authentication service contract.
 *
 * <p>Covers user registration and login.  Both operations return an
 * {@link AuthResponse} so callers immediately have a usable JWT token.
 */
public interface AuthService {

    /**
     * Register a new user account.
     *
     * @param request validated registration payload
     * @return {@link AuthResponse} containing a signed JWT for immediate login
     * @throws com.kafka.shikhar.app.exception.UserAlreadyExistsException if
     *         the email or username is already in use
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Authenticate an existing user.
     *
     * @param request login credentials (email + password)
     * @return {@link AuthResponse} containing a signed JWT
     * @throws org.springframework.security.core.AuthenticationException on bad credentials
     */
    AuthResponse login(LoginRequest request);
}
