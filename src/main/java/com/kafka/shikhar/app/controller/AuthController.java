package com.kafka.shikhar.app.controller;

import com.kafka.shikhar.app.dto.request.LoginRequest;
import com.kafka.shikhar.app.dto.request.RegisterRequest;
import com.kafka.shikhar.app.dto.response.AuthResponse;
import com.kafka.shikhar.app.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication operations.
 *
 * <h2>Endpoints</h2>
 * <table>
 *   <tr><th>Method</th><th>Path</th><th>Auth</th><th>Description</th></tr>
 *   <tr><td>POST</td><td>/api/auth/register</td><td>Public</td><td>Register a new user</td></tr>
 *   <tr><td>POST</td><td>/api/auth/login</td><td>Public</td><td>Obtain a JWT token</td></tr>
 * </table>
 *
 * <p>Both endpoints are excluded from JWT verification in {@link com.kafka.shikhar.app.config.SecurityConfig}.
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Register a new user account and return an immediate JWT.
     *
     * @param request validated registration payload
     * @return 201 Created with {@link AuthResponse}
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("AuthController → POST /api/auth/register for email={}", request.getEmail());
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Authenticate an existing user and return a JWT.
     *
     * @param request login credentials (email + password)
     * @return 200 OK with {@link AuthResponse}
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("AuthController → POST /api/auth/login for email={}", request.getEmail());
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
