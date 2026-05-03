package com.kafka.shikhar.app.dao.impl;

import com.kafka.shikhar.app.dao.UserDao;
import com.kafka.shikhar.app.entity.UserEntity;
import com.kafka.shikhar.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Default implementation of {@link UserDao} backed by {@link UserRepository}.
 *
 * <p>This class acts as a thin delegation layer.  Its main value is to:
 * <ul>
 *   <li>Keep Spring Data interfaces out of the service layer.</li>
 *   <li>Provide a single place to add cross-cutting concerns (metrics, secondary
 *       cache lookup, etc.) without touching business logic.</li>
 * </ul>
 *
 * <p>Logging is done at {@code DEBUG} level so production logs stay clean
 * while being verbose during local development.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class UserDaoImpl implements UserDao {

    private final UserRepository userRepository;

    @Override
    public UserEntity save(UserEntity user) {
        log.debug("DAO → persisting user with email={}", user.getEmail());
        UserEntity saved = userRepository.save(user);
        log.debug("DAO → user persisted with id={}", saved.getId());
        return saved;
    }

    @Override
    public List<UserEntity> findAll() {
        log.debug("DAO → fetching all users");
        List<UserEntity> users = userRepository.findAll();
        log.debug("DAO → fetched {} user(s)", users.size());
        return users;
    }

    @Override
    public Optional<UserEntity> findById(Long id) {
        log.debug("DAO → looking up user by id={}", id);
        return userRepository.findById(id);
    }

    @Override
    public Optional<UserEntity> findByEmail(String email) {
        log.debug("DAO → looking up user by email={}", email);
        return userRepository.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        boolean exists = userRepository.existsByEmail(email);
        log.debug("DAO → existsByEmail({}) = {}", email, exists);
        return exists;
    }

    @Override
    public boolean existsByUsername(String username) {
        boolean exists = userRepository.existsByUsername(username);
        log.debug("DAO → existsByUsername({}) = {}", username, exists);
        return exists;
    }
}
