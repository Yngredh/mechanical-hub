package com.fiap.mechanical_hub.application.mappers;

import com.fiap.mechanical_hub.application.dto.servicematerials.ServiceMaterialResponse;
import com.fiap.mechanical_hub.application.dto.service.ServiceResponse;
import com.fiap.mechanical_hub.domain.entities.Material;
import com.fiap.mechanical_hub.domain.entities.Service;
import com.fiap.mechanical_hub.domain.entities.ServiceMaterial;
import com.fiap.mechanical_hub.infrastructure.database.models.MaterialModel;
import com.fiap.mechanical_hub.infrastructure.database.models.ServiceMaterialModel;
import com.fiap.mechanical_hub.infrastructure.database.models.ServiceModel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ServiceMapper {

    private ServiceMapper() {}

    public static Service toDomainEntity(ServiceModel model) {
        List<ServiceMaterial> materials = new ArrayList<>();

        if (model.getMaterials() != null) {
            materials = model.getMaterials().stream()
                    .map(sm -> {
                        Material material = MaterialMapper.toDomainEntity(sm.getMaterial());
                        return new ServiceMaterial(sm.getId(), material, sm.getQuantity());
                    })
                    .toList();
        }

        return new Service(
                model.getId(),
                model.getName(),
                model.getDescription(),
                model.getLaborCost(),
                model.getBasePrice(),
                model.getTotalPrice(),
                materials
        );
    }

    public static ServiceModel toJpaEntity(Service service) {
        ServiceModel model = new ServiceModel(
                service.getId(),
                service.getName(),
                service.getDescription(),
                service.getLaborCost(),
                service.getBasePrice(),
                service.getTotalPrice(),
                service.isActive(),
                service.getCreatedAt(),
                service.getUpdatedAt()
        );

        if (service.getMaterials() != null) {
            List<ServiceMaterialModel> materials = service.getMaterials().stream()
                    .map(sm -> toServiceMaterialModel(sm, model))
                    .toList();
            model.setMaterials(new ArrayList<>(materials));
        }

        return model;
    }

    public static ServiceMaterialModel toServiceMaterialModel(ServiceMaterial serviceMaterial, ServiceModel serviceModel) {
        MaterialModel materialModel = new MaterialModel(
                serviceMaterial.material().getId(),
                serviceMaterial.material().getName(),
                serviceMaterial.material().getDescription(),
                serviceMaterial.material().getUnitPrice(),
                serviceMaterial.material().getMinStockQuantity(),
                serviceMaterial.material().getCreatedAt(),
                serviceMaterial.material().getUpdatedAt()
        );

        return new ServiceMaterialModel(
                serviceMaterial.id(),
                serviceModel,
                materialModel,
                serviceMaterial.quantity()
        );
    }

    public static ServiceResponse toResponse(Service service) {
        List<ServiceMaterialResponse> materialsResponse = new ArrayList<>();

        if (service.getMaterials() != null) {
            materialsResponse = service.getMaterials().stream()
                    .map(sm -> new ServiceMaterialResponse(
                            sm.material().getId(),
                            sm.material().getName(),
                            sm.material().getDescription(),
                            sm.material().getUnitPrice(),
                            sm.quantity(),
                            sm.calculateCost()
                    ))
                    .toList();
        }

        return new ServiceResponse(
                service.getId(),
                service.getName(),
                service.getDescription(),
                service.getLaborCost(),
                service.getBasePrice(),
                service.getTotalPrice(),
                materialsResponse,
                service.isActive(),
                service.getCreatedAt(),
                service.getUpdatedAt()
        );
    }
}
