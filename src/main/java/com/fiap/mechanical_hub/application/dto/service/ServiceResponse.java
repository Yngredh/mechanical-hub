package com.fiap.mechanical_hub.application.dto.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class ServiceResponse {

    private UUID id;
    private String name;
    private String description;
    private BigDecimal laborCost;
    private BigDecimal basePrice;
    private BigDecimal totalPrice;
    private List<ServiceMaterialResponse> materials;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

