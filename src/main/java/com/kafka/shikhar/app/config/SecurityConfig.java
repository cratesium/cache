package com.kafka.shikhar.app.config;

import com.kafka.shikhar.app.security.JwtAuthenticationFilter;
import com.kafka.shikhar.app.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Central Spring Security configuration.
 *
 * <h2>Design decisions</h2>
 * <ul>
 *   <li><b>Stateless sessions</b> – no server-side session is created;
 *       every request must carry a valid JWT.</li>
 *   <li><b>CSRF disabled</b> – appropriate for REST APIs with JWT-based auth.</li>
 *   <li><b>BCrypt</b> – work-factor 12 (default); increase for higher security at the cost
 *       of CPU during login.</li>
 *   <li><b>Public routes</b> – {@code /api/auth/**} and {@code /app/health} are open;
 *       everything else requires a valid JWT.</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity          // enables @PreAuthorize / @PostAuthorize on methods
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsServiceImpl   userDetailsService;

    /** Public endpoints that do NOT require authentication. */
    private static final String[] PUBLIC_URLS = {
            "/api/auth/**",
            "/app/health"
    };

    // ─── Security Filter Chain ────────────────────────────────────────────────

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF – not needed for stateless REST + JWT
                .csrf(AbstractHttpConfigurer::disable)

                // Route authorisation
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_URLS).permitAll()
                        .anyRequest().authenticated()
                )

                // Stateless session (no HttpSession)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Use our custom auth provider
                .authenticationProvider(authenticationProvider())

                // JWT filter runs before Spring's username/password filter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ─── Authentication Beans ─────────────────────────────────────────────────

    /**
     * DAO-backed authentication provider wired to our {@link UserDetailsServiceImpl}
     * and BCrypt password encoder.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * Expose the {@link AuthenticationManager} so the auth service can call
     * {@code authenticate()} directly.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * BCrypt encoder – strength 12 is a good balance for modern hardware.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
