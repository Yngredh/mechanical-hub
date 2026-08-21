package com.fiap.mechanical_hub.application.dto.user;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        String documentNumber,
        String profile
) {}

