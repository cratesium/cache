package com.kafka.shikhar.app.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * JPA Entity representing a registered user in the system.
 *
 * <p>Implements {@link UserDetails} so Spring Security can directly use this
 * entity for authentication / authorization without a separate adapter layer.
 *
 * <p>Fields:
 * <ul>
 *   <li>{@code id}        - auto-generated primary key</li>
 *   <li>{@code name}      - full display name</li>
 *   <li>{@code email}     - unique, used as the login username</li>
 *   <li>{@code username}  - unique handle / alias</li>
 *   <li>{@code password}  - BCrypt-hashed password (never stored in plain text)</li>
 *   <li>{@code role}      - single role for simplicity; extend to Set<Role> for RBAC</li>
 *   <li>{@code enabled}   - soft-disable without deleting the row</li>
 *   <li>{@code createdAt} - auto-set on INSERT</li>
 *   <li>{@code updatedAt} - auto-set on UPDATE</li>
 * </ul>
 */
@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_users_email",    columnNames = "email"),
                @UniqueConstraint(name = "uk_users_username", columnNames = "username")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(nullable = false)
    @Builder.Default
    private boolean enabled = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ─── UserDetails contract ──────────────────────────────────────────────────

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    /** Email is used as the principal (login identifier). */
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired()  { return true; }

    @Override
    public boolean isAccountNonLocked()   { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return enabled; }
}
