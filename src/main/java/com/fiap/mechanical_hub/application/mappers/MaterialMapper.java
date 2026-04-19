package com.fiap.mechanical_hub.application.mappers;

import com.fiap.mechanical_hub.application.dto.material.MaterialResponse;
import com.fiap.mechanical_hub.domain.entities.Material;
import com.fiap.mechanical_hub.infrastructure.database.models.MaterialModel;
import org.springframework.stereotype.Component;

@Component
public class MaterialMapper {

    public static Material toDomainEntity(MaterialModel register) {
        return new Material(
                register.getId(),
                register.getName(),
                register.getDescription(),
                register.getUnitPrice(),
                register.getMinStockQuantity(),
                register.getCreatedAt(),
                register.getUpdatedAt()
        );
    }

    public static MaterialModel toJpaEntity(Material material) {
        return new MaterialModel(
                material.getId(),
                material.getName(),
                material.getDescription(),
                material.getUnitPrice(),
                material.getMinStockQuantity(),
                material.getCreatedAt(),
                material.getUpdatedAt()
        );
    }

    public MaterialResponse toResponse(Material material) {
        return new MaterialResponse(
                material.getId(),
                material.getName(),
                material.getDescription(),
                material.getUnitPrice(),
                material.getMinStockQuantity(),
                material.getCreatedAt(),
                material.getUpdatedAt()
        );
    }
}

