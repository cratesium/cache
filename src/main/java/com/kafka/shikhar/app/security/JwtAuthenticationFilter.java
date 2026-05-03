package com.kafka.shikhar.app.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT authentication filter – executes once per HTTP request.
 *
 * <h2>Flow</h2>
 * <ol>
 *   <li>Extract the {@code Authorization: Bearer &lt;token&gt;} header.</li>
 *   <li>If absent or malformed, skip and continue the filter chain (unauthenticated).</li>
 *   <li>Parse the token to extract the subject (email).</li>
 *   <li>Load the user from {@link UserDetailsServiceImpl}.</li>
 *   <li>Validate the token; if valid, populate the {@link SecurityContextHolder}.</li>
 *   <li>Continue the filter chain – Spring Security then enforces route authorisation.</li>
 * </ol>
 *
 * <p>Extending {@link OncePerRequestFilter} guarantees the filter logic runs
 * exactly once per request even in async dispatch scenarios.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // ── 1. Skip if no Bearer token is present ────────────────────────────
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            log.trace("JwtFilter → no Bearer token in request to [{}]", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        // ── 2. Extract token ──────────────────────────────────────────────────
        final String jwt = authHeader.substring(BEARER_PREFIX.length());
        String subject;
        try {
            subject = jwtUtil.extractSubject(jwt);
        } catch (Exception ex) {
            log.warn("JwtFilter → failed to extract subject from token: {}", ex.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        // ── 3. Authenticate if not already in SecurityContext ─────────────────
        if (subject != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = userDetailsService.loadUserByUsername(subject);

            if (jwtUtil.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.debug("JwtFilter → authenticated user={} for [{}]", subject, request.getRequestURI());
            } else {
                log.warn("JwtFilter → invalid token for user={}", subject);
            }
        }

        filterChain.doFilter(request, response);
    }
}
