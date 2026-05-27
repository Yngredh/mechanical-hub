package com.fiap.mechanical_hub.domain.entities;

import com.fiap.mechanical_hub.domain.exceptions.BusinessRuleException;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor(force = true)
@Builder
public class ServiceData {

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
    private LocalDateTime deletedAt;

    public ServiceData(UUID id, String name, String description, BigDecimal laborCost, BigDecimal basePrice,
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
        this.deletedAt = null;
    }

    public ServiceData(UUID id, String name, String description, BigDecimal laborCost, BigDecimal basePrice,
                       BigDecimal totalPrice, List<ServiceMaterial> materials, boolean active, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
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
        this.deletedAt = deletedAt;
    }

    public static ServiceData create(String name, String description, BigDecimal laborCost,
                                     BigDecimal basePrice, List<ServiceMaterial> materials) {
        ServiceData serviceData = new ServiceData(
                UUID.randomUUID(), name, description, laborCost, basePrice,BigDecimal.ZERO, materials, true,
                LocalDateTime.now(), LocalDateTime.now()
        );

        serviceData.validateInputs();
        serviceData.recalculateTotalPrice();

        return serviceData;
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
        this.deletedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return this.deletedAt == null;
    }
}
