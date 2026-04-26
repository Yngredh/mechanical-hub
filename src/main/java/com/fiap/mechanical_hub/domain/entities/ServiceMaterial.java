package com.fiap.mechanical_hub.domain.entities;

import com.fiap.mechanical_hub.domain.exceptions.BusinessRuleException;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class ServiceMaterial {

    private UUID id;
    private UUID serviceId;
    private Material material;
    private int quantity;

    public ServiceMaterial(UUID id, UUID serviceId, Material material, int quantity) {
        validate(material, quantity);

        this.id = id;
        this.serviceId = serviceId;
        this.material = material;
        this.quantity = quantity;
    }

    public static ServiceMaterial create(Material material, int quantity) {
        validate(material, quantity);

        ServiceMaterial serviceMaterial = new ServiceMaterial();
        serviceMaterial.id = UUID.randomUUID();
        serviceMaterial.material = material;
        serviceMaterial.quantity = quantity;

        return serviceMaterial;
    }

    private static void validate(Material material, int quantity) {
        if (material == null) { throw new BusinessRuleException("Material is required"); }
        if (quantity <= 0) { throw new BusinessRuleException("Quantity must be greater than zero"); }
    }

    public BigDecimal calculateCost() {
        return material.getUnitPrice()
                .multiply(BigDecimal.valueOf(quantity));
    }
}