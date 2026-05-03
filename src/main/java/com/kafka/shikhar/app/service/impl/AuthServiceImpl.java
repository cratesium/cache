package com.kafka.shikhar.app.service.impl;

import com.kafka.shikhar.app.dao.UserDao;
import com.kafka.shikhar.app.dto.request.LoginRequest;
import com.kafka.shikhar.app.dto.request.RegisterRequest;
import com.kafka.shikhar.app.dto.response.AuthResponse;
import com.kafka.shikhar.app.entity.Role;
import com.kafka.shikhar.app.entity.UserEntity;
import com.kafka.shikhar.app.exception.UserAlreadyExistsException;
import com.kafka.shikhar.app.security.JwtUtil;
import com.kafka.shikhar.app.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation of {@link AuthService}.
 *
 * <h2>Registration flow</h2>
 * <ol>
 *   <li>Validate that email and username are not already taken.</li>
 *   <li>BCrypt-hash the plain-text password.</li>
 *   <li>Persist the new {@link UserEntity} via the DAO.</li>
 *   <li>Generate and return a signed JWT so the user is immediately logged in.</li>
 * </ol>
 *
 * <h2>Login flow</h2>
 * <ol>
 *   <li>Delegate to {@link AuthenticationManager} (performs credential check).</li>
 *   <li>On success, extract the {@link UserEntity} principal.</li>
 *   <li>Generate and return a signed JWT.</li>
 * </ol>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserDao               userDao;
    private final PasswordEncoder       passwordEncoder;
    private final JwtUtil               jwtUtil;
    private final AuthenticationManager authenticationManager;

    // ─── Registration ─────────────────────────────────────────────────────────

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("AuthService → registering new user email={}", request.getEmail());

        // Guard: duplicate email
        if (userDao.existsByEmail(request.getEmail())) {
            log.warn("AuthService → email already registered: {}", request.getEmail());
            throw new UserAlreadyExistsException(
                    "Email is already registered: " + request.getEmail());
        }

        // Guard: duplicate username
        if (userDao.existsByUsername(request.getUsername())) {
            log.warn("AuthService → username already taken: {}", request.getUsername());
            throw new UserAlreadyExistsException(
                    "Username is already taken: " + request.getUsername());
        }

        // Build and persist entity
        UserEntity user = UserEntity.builder()
                .name(request.getName())
                .email(request.getEmail().toLowerCase())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .enabled(true)
                .build();

        UserEntity saved = userDao.save(user);
        log.info("AuthService → user registered successfully id={} email={}",
                saved.getId(), saved.getEmail());

        // Issue JWT immediately (auto-login after register)
        String token = jwtUtil.generateToken(saved);
        return AuthResponse.builder()
                .accessToken(token)
                .expiresAt(jwtUtil.getExpiresAt())
                .build();
    }

    // ─── Login ────────────────────────────────────────────────────────────────

    @Override
    public AuthResponse login(LoginRequest request) {
        log.info("AuthService → login attempt for email={}", request.getEmail());

        // Spring Security validates credentials; throws BadCredentialsException on failure
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        UserEntity user = (UserEntity) authentication.getPrincipal();
        String token = jwtUtil.generateToken(user);

        log.info("AuthService → login successful for email={}", user.getEmail());

        return AuthResponse.builder()
                .accessToken(token)
                .expiresAt(jwtUtil.getExpiresAt())
                .build();
    }
}
