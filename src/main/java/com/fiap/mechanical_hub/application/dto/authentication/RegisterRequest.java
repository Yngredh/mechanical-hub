package com.fiap.mechanical_hub.application.dto.authentication;

import jakarta.validation.constraints.*;

public record RegisterRequest(

        @NotBlank
        @Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ ]+$", message = "Name must contain only letters")
        String name,

        @NotBlank
        @Email(message = "Login must be a valid email")
        String login,

        @NotBlank
        @Size(min = 6, message = "Password must have at least 6 characters")
        String password,

        @NotBlank
        @Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ ]+$", message = "Name must contain only letters")
        String profile
) {}