package com.kafka.shikhar.app.service.impl;

import com.kafka.shikhar.app.dao.UserDao;
import com.kafka.shikhar.app.dto.response.UserResponse;
import com.kafka.shikhar.app.entity.UserEntity;
import com.kafka.shikhar.app.exception.UserNotFoundException;
import com.kafka.shikhar.app.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Default implementation of {@link UserService}.
 *
 * <p>All public methods map {@link UserEntity} objects to {@link UserResponse} DTOs
 * before returning them so the caller never touches raw JPA entities.
 *
 * <p>The {@code @Transactional(readOnly = true)} annotation on read methods tells
 * Hibernate to skip dirty checking for a modest performance improvement.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    // ─── Read ─────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        log.info("UserService → fetching all users");
        List<UserResponse> users = userDao.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
        log.info("UserService → returned {} user(s)", users.size());
        return users;
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        log.info("UserService → fetching user id={}", id);
        return userDao.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> {
                    log.warn("UserService → user not found id={}", id);
                    return new UserNotFoundException("User not found with id: " + id);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getProfile(String email) {
        log.info("UserService → fetching profile for email={}", email);
        return userDao.findByEmail(email)
                .map(this::toResponse)
                .orElseThrow(() -> {
                    log.warn("UserService → profile not found for email={}", email);
                    return new UserNotFoundException("User not found with email: " + email);
                });
    }

    // ─── Delete ───────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void deleteUser(Long id) {
        log.info("UserService → disabling user id={}", id);
        UserEntity user = userDao.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        user.setEnabled(false);
        userDao.save(user);
        log.info("UserService → user id={} disabled", id);
    }

    // ─── Mapper ───────────────────────────────────────────────────────────────

    /**
     * Manual mapping from {@link UserEntity} to {@link UserResponse}.
     * A MapStruct mapper can replace this once the project grows.
     *
     * @param entity the source JPA entity
     * @return a safe public-facing DTO
     */
    private UserResponse toResponse(UserEntity entity) {
        return UserResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .username(entity.getUsername())
                .role(entity.getRole())
                .enabled(entity.isEnabled())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
