package com.fiap.mechanical_hub.application.mappers;

import com.fiap.mechanical_hub.domain.entities.Material;
import com.fiap.mechanical_hub.domain.entities.ServiceMaterial;
import com.fiap.mechanical_hub.infrastructure.database.models.ServiceMaterialModel;
import com.fiap.mechanical_hub.infrastructure.database.models.ServiceModel;
import lombok.NoArgsConstructor;

public class ServiceMaterialMapper {

    public static ServiceMaterialModel toJpaEntity(ServiceMaterial entity, ServiceModel parentService) {
        return new ServiceMaterialModel(
                entity.getId(),
                parentService,
                MaterialMapper.toJpaEntity(entity.getMaterial()),
                entity.getQuantity()
        );
    }

    public static ServiceMaterial toDomainEntity(ServiceMaterialModel model) {
        return new ServiceMaterial(
                model.getId(),
                model.getService().getId(),
                new Material(
                        model.getMaterial().getId(),
                        model.getMaterial().getName(),
                        model.getMaterial().getDescription(),
                        model.getMaterial().getUnitPrice(),
                        model.getMaterial().getMinStockQuantity(),
                        model.getMaterial().getCreatedAt(),
                        model.getService().getUpdatedAt()
                ),
                model.getQuantity()
        );
    }
}
