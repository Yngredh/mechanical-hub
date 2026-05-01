package com.fiap.mechanical_hub.application.mappers;

import com.fiap.mechanical_hub.application.dto.service.ServiceResponse;
import com.fiap.mechanical_hub.domain.entities.Material;
import com.fiap.mechanical_hub.domain.entities.ServiceData;
import com.fiap.mechanical_hub.domain.entities.ServiceMaterial;
import com.fiap.mechanical_hub.infrastructure.database.models.ServiceMaterialModel;
import com.fiap.mechanical_hub.infrastructure.database.models.ServiceModel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ServiceMapper {

    private ServiceMapper() {}

    public static ServiceData toDomainEntity(ServiceModel model) {
        List<ServiceMaterial> materials = new ArrayList<>();

        if (model.getMaterials() != null) {
            materials = model.getMaterials().stream()
                    .map(sm -> {
                        Material material = MaterialMapper.toDomainEntity(sm.getMaterial());
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
                model.getUpdatedAt()
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

        for (ServiceMaterial item : serviceData.getMaterials()) {
            ServiceMaterialModel child = new ServiceMaterialModel();
            child.setId(item.getId());
            child.setMaterial(MaterialMapper.toJpaEntity(item.getMaterial()));
            child.setQuantity(item.getQuantity());
            child.setService(model);
            model.getMaterials().add(child);
        }

        return model;
    }

    public static ServiceResponse toResponse(ServiceData serviceData) {
        return new ServiceResponse(
                serviceData.getId(),
                serviceData.getName(),
                serviceData.getDescription(),
                serviceData.getLaborCost(),
                serviceData.getBasePrice(),
                serviceData.getTotalPrice(),
                serviceData.getMaterials().stream().map(ServiceMaterialMapper::toResponse).toList(),
                serviceData.isActive(),
                serviceData.getCreatedAt(),
                serviceData.getUpdatedAt()
        );
    }
}
