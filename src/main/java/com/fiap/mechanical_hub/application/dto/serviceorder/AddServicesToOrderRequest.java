package com.fiap.mechanical_hub.application.dto.serviceorder;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public record AddServicesToOrderRequest(
        @NotEmpty(message = "Lista de IDs de serviços não pode estar vazia")
        List<UUID> serviceIds
) { }

