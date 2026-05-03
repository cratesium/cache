package com.kafka.shikhar.app.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Inbound payload for the <em>POST /api/auth/register</em> endpoint.
 *
 * <p>All fields are validated via Bean Validation annotations so the
 * controller layer stays thin.  Violations are surfaced as 400 responses
 * by {@link com.kafka.shikhar.app.exception.GlobalExceptionHandler}.
 */
@Data
public class RegisterRequest {

    /** Full display name of the user. */
    @NotBlank(message = "Name must not be blank")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    /** Unique login email address. */
    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email must be a valid email address")
    private String email;

    /** Unique public handle / alias. */
    @NotBlank(message = "Username must not be blank")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    /** Plain-text password – will be BCrypt-hashed before persistence. */
    @NotBlank(message = "Password must not be blank")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
}
