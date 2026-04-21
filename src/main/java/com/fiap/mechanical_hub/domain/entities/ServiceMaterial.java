package com.fiap.mechanical_hub.domain.entities;

import com.fiap.mechanical_hub.domain.exceptions.BusinessRuleException;

import java.math.BigDecimal;
import java.util.UUID;

public record ServiceMaterial(UUID id, Material material, int quantity) {

    public ServiceMaterial {
        validate(material, quantity);

    }

    public ServiceMaterial(Material material, int quantity) {
        this(UUID.randomUUID(), material, quantity);
    }

    private void validate(Material material, int quantity) {
        if (material == null) {
            throw new BusinessRuleException("Material is required");
        }

        if (quantity <= 0) {
            throw new BusinessRuleException("Quantity must be greater than zero");
        }
    }

    public BigDecimal calculateCost() {
        return material.getUnitPrice()
                .multiply(BigDecimal.valueOf(quantity));
    }
}