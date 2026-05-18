package com.fiap.mechanical_hub.infrastructure.database.mappers;

import com.fiap.mechanical_hub.domain.entities.Material;
import com.fiap.mechanical_hub.infrastructure.database.models.MaterialModel;

public class MaterialRepositoryMapper {

    private MaterialRepositoryMapper() {
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

    public static Material toDomainEntity(MaterialModel entity) {
        return new Material(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getUnitPrice(),
                entity.getMinStockQuantity(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

}


