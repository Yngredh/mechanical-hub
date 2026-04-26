package com.fiap.mechanical_hub.domain.entities;

import com.fiap.mechanical_hub.domain.exceptions.BusinessRuleException;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
public class Service {

    private final UUID id;
    private String name;
    private String description;
    private BigDecimal laborCost;
    private BigDecimal basePrice;
    private BigDecimal totalPrice;
    private List<ServiceMaterial> materials;
    private boolean active;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Service(UUID id, String name, String description, BigDecimal laborCost, BigDecimal basePrice,
                   BigDecimal totalPrice, List<ServiceMaterial> materials, boolean active, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.laborCost = laborCost;
        this.basePrice = basePrice;
        this.totalPrice = totalPrice;
        this.materials = materials;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Service create(
            String name,
            String description,
            BigDecimal laborCost,
            BigDecimal basePrice,
            List<ServiceMaterial> materials
    ) {
        Service service = new Service(
                UUID.randomUUID(),
                name,
                description,
                laborCost,
                basePrice,
                BigDecimal.ZERO,
                materials,
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        service.validateInputs();
        service.recalculateTotalPrice();

        return service;
    }

    public void update(
            String name,
            String description,
            BigDecimal laborCost,
            BigDecimal basePrice,
            List<ServiceMaterial> materials
    ) {
        this.name = name;
        this.description = description;
        this.laborCost = laborCost;
        this.basePrice = basePrice;
        this.materials = materials;
        this.updatedAt = LocalDateTime.now();

        validateInputs();
        recalculateTotalPrice();
    }

    private void recalculateTotalPrice() {
        BigDecimal materialsCost = materials.stream()
                .map(m -> m.getMaterial().getUnitPrice()
                        .multiply(BigDecimal.valueOf(m.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.totalPrice = laborCost.add(materialsCost);
    }

    private void validateInputs() {
        if (name == null || name.isBlank()) {
            throw new BusinessRuleException("Service name is required");
        }

        if (laborCost == null || laborCost.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessRuleException("Labor cost must be positive");
        }

        if (basePrice == null || basePrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessRuleException("Base price must be positive");
        }

        if (materials == null) {
            throw new BusinessRuleException("Materials cannot be null");
        }
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }
}
