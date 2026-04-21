package com.fiap.mechanical_hub.application.dto.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpsertServiceRequest {

    @NotBlank(message = "Service name is required")
    private String name;

    @NotBlank(message = "Service description is required")
    private String description;

    @NotNull(message = "Labor cost is required")
    @PositiveOrZero(message = "Labor cost must be positive or zero")
    private BigDecimal laborCost;

    @NotNull(message = "Base price is required")
    @PositiveOrZero(message = "Base price must be positive or zero")
    private BigDecimal basePrice;

    @NotEmpty(message = "At least one material is required")
    @Valid
    private List<ServiceMaterialRequest> materials;
}