package com.kafka.shikhar.app.entity;

/**
 * Application-level roles.
 *
 * <p>Stored as a VARCHAR in the DB via {@code @Enumerated(EnumType.STRING)}.
 * To extend permissions, add more values here and wire them into
 * {@link UserEntity#getAuthorities()}.
 */
public enum Role {
    USER,
    ADMIN
}
