package com.kafka.shikhar.app.controller;

import com.kafka.shikhar.app.dto.response.UserResponse;
import com.kafka.shikhar.app.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for user management operations.
 *
 * <h2>Endpoints</h2>
 * <table>
 *   <tr><th>Method</th><th>Path</th><th>Role</th><th>Description</th></tr>
 *   <tr><td>GET</td><td>/api/users/me</td><td>USER / ADMIN</td><td>Current user profile</td></tr>
 *   <tr><td>GET</td><td>/api/users</td><td>ADMIN</td><td>List all users</td></tr>
 *   <tr><td>GET</td><td>/api/users/{id}</td><td>ADMIN</td><td>Get user by id</td></tr>
 *   <tr><td>DELETE</td><td>/api/users/{id}</td><td>ADMIN</td><td>Soft-delete user</td></tr>
 * </table>
 *
 * <p>Method-level {@code @PreAuthorize} is preferred over URL patterns for
 * fine-grained, code-colocated access control.
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Return the authenticated user's own profile.
     *
     * @param principal injected by Spring Security from the JWT context
     * @return 200 OK with the caller's {@link UserResponse}
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyProfile(
            @AuthenticationPrincipal UserDetails principal) {
        log.info("UserController → GET /api/users/me for email={}", principal.getUsername());
        return ResponseEntity.ok(userService.getProfile(principal.getUsername()));
    }

    /**
     * List all registered users. Restricted to ADMIN role.
     *
     * @return 200 OK with a list of {@link UserResponse}
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        log.info("UserController → GET /api/users");
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * Get a specific user by their primary key. Restricted to ADMIN role.
     *
     * @param id the user's primary key
     * @return 200 OK with the matching {@link UserResponse}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        log.info("UserController → GET /api/users/{}", id);
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * Soft-delete (disable) a user account. Restricted to ADMIN role.
     *
     * @param id the user's primary key
     * @return 204 No Content on success
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("UserController → DELETE /api/users/{}", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
