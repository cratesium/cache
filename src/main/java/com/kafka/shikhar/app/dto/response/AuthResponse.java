package com.kafka.shikhar.app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response returned after a successful login or registration.
 *
 * <p>Contains the signed JWT ({@code accessToken}) and its type
 * so callers can compose the {@code Authorization: Bearer <token>} header.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    /** Signed JWT – include as {@code Authorization: Bearer <accessToken>}. */
    private String accessToken;

    /** Always {@code "Bearer"} – included for completeness per RFC 6750. */
    @Builder.Default
    private String tokenType = "Bearer";

    /** Epoch-millisecond timestamp at which the token expires. */
    private long expiresAt;
}
