package com.fiap.mechanical_hub.infrastructure.security;

import java.util.UUID;

public record GatewayPrincipal(UUID id, String name, String role) {

    @Override
    public String toString() {
        return id != null ? id.toString() : "anonymous";
    }
}
