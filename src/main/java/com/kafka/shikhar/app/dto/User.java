package com.kafka.shikhar.app.dto;

/**
 * @deprecated Use {@link com.kafka.shikhar.app.dto.response.UserResponse} for outbound
 *             payloads and {@link com.kafka.shikhar.app.dto.request.RegisterRequest} /
 *             {@link com.kafka.shikhar.app.dto.request.LoginRequest} for inbound payloads.
 *
 *             This class is kept to prevent compile errors from leftover references
 *             and will be removed in the next cleanup sprint.
 */
@Deprecated(since = "0.2.0", forRemoval = true)
public class User {
    // intentionally empty – superseded by request/response DTOs
}
