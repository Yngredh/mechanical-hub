package com.fiap.mechanical_hub.infrastructure.database.mappers;

import com.fiap.mechanical_hub.domain.entities.Material;
import com.fiap.mechanical_hub.domain.entities.ServiceData;
import com.fiap.mechanical_hub.domain.entities.ServiceMaterial;
import com.fiap.mechanical_hub.infrastructure.database.models.ServiceMaterialModel;
import com.fiap.mechanical_hub.infrastructure.database.models.ServiceModel;

import java.util.ArrayList;
import java.util.List;

public class ServiceRepositoryMapper {

    private ServiceRepositoryMapper() {
    }

    public static ServiceData toDomainEntity(ServiceModel model) {
        List<ServiceMaterial> materials = new ArrayList<>();

        if (model.getMaterials() != null) {
            materials = model.getMaterials().stream()
                    .map(sm -> {
                        Material material = MaterialRepositoryMapper.toDomainEntity(sm.getMaterial());
                        return ServiceMaterial.create(material, sm.getQuantity());
                    })
                    .toList();
        }

        return new ServiceData(
                model.getId(),
                model.getName(),
                model.getDescription(),
                model.getLaborCost(),
                model.getBasePrice(),
                model.getTotalPrice(),
                materials,
                model.isActive(),
                model.getCreatedAt(),
                model.getUpdatedAt(),
                model.getDeletedAt()
        );
    }

    public static ServiceModel toJpaEntity(ServiceData serviceData) {
        ServiceModel model = new ServiceModel();

        model.setId(serviceData.getId());
        model.setName(serviceData.getName());
        model.setDescription(serviceData.getDescription());
        model.setLaborCost(serviceData.getLaborCost());
        model.setBasePrice(serviceData.getBasePrice());
        model.setTotalPrice(serviceData.getTotalPrice());
        model.setCreatedAt(serviceData.getCreatedAt());
        model.setUpdatedAt(serviceData.getUpdatedAt());
        model.setActive(serviceData.isActive());
        model.setDeletedAt(serviceData.getDeletedAt());

        for (ServiceMaterial item : serviceData.getMaterials()) {
            ServiceMaterialModel child = new ServiceMaterialModel();
            child.setId(item.getId());
            child.setMaterial(MaterialRepositoryMapper.toJpaEntity(item.getMaterial()));
            child.setQuantity(item.getQuantity());
            child.setService(model);
            model.getMaterials().add(child);
        }

        return model;
    }
}
