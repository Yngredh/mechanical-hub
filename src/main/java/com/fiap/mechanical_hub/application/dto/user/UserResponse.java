package com.fiap.mechanical_hub.application.dto.user;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        String profile,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}

