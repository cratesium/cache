package com.kafka.shikhar.app.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utility class for creating, parsing, and validating JSON Web Tokens (JWT).
 *
 * <p>Tokens are signed with an HMAC-SHA-256 key derived from the configured secret.
 * The expiry window is configurable via {@code application.properties}.
 *
 * <h2>Token structure</h2>
 * <pre>
 *   Header : { "alg": "HS256" }
 *   Payload: { "sub": "&lt;email&gt;", "iat": &lt;issuedAt&gt;, "exp": &lt;expiry&gt; }
 *   Signature: HMAC-SHA256(base64(header) + "." + base64(payload), secret)
 * </pre>
 *
 * <h2>Usage</h2>
 * <ol>
 *   <li>Call {@link #generateToken(UserDetails)} after successful authentication.</li>
 *   <li>Call {@link #isTokenValid(String, UserDetails)} inside the JWT filter on every request.</li>
 * </ol>
 */
@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    // ─── Token Generation ──────────────────────────────────────────────────────

    /**
     * Generate a signed JWT for the given user.
     *
     * @param userDetails the authenticated principal
     * @return a compact JWT string
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Generate a signed JWT with additional custom claims.
     *
     * @param extraClaims key-value pairs to embed in the payload
     * @param userDetails the authenticated principal
     * @return a compact JWT string
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        long now = System.currentTimeMillis();
        String token = Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(now))
                .expiration(new Date(now + expirationMs))
                .signWith(getSigningKey())
                .compact();
        log.debug("JwtUtil → generated token for subject={}", userDetails.getUsername());
        return token;
    }

    // ─── Token Validation ──────────────────────────────────────────────────────

    /**
     * Validate a JWT against the provided user details.
     *
     * @param token       the JWT to validate
     * @param userDetails the principal to match against
     * @return {@code true} if the token is genuine, unexpired, and belongs to the user
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            String subject = extractSubject(token);
            return subject.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException ex) {
            log.warn("JwtUtil → invalid token: {}", ex.getMessage());
            return false;
        }
    }

    // ─── Claim Extraction ─────────────────────────────────────────────────────

    /**
     * Extract the subject (email / username) from a JWT.
     *
     * @param token the JWT string
     * @return the subject claim value
     */
    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract the expiry date from a JWT.
     *
     * @param token the JWT string
     * @return the expiration {@link Date}
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Generic claim extractor using a resolver function.
     *
     * @param token    the JWT string
     * @param resolver function to apply on the {@link Claims} object
     * @param <T>      the return type
     * @return the resolved claim value
     */
    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    // ─── Internals ────────────────────────────────────────────────────────────

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Convenience method – returns the epoch-millisecond expiry of a freshly generated token.
     *
     * @return epoch ms when the next issued token will expire
     */
    public long getExpiresAt() {
        return System.currentTimeMillis() + expirationMs;
    }
}
