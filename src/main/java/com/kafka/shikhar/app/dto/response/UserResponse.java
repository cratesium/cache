package com.kafka.shikhar.app.dto.response;

import com.kafka.shikhar.app.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Safe view of a {@link com.kafka.shikhar.app.entity.UserEntity} returned to callers.
 *
 * <p>Never exposes the hashed password or internal JPA fields.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private String username;
    private Role role;
    private boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
