package com.fiap.mechanical_hub.application.mappers;

import com.fiap.mechanical_hub.application.command.ordertask.CreateServiceCommand;
import com.fiap.mechanical_hub.application.command.ordertask.UpdateServiceCommand;
import com.fiap.mechanical_hub.application.dto.service.ServiceResponse;
import com.fiap.mechanical_hub.application.dto.service.UpsertServiceRequest;
import com.fiap.mechanical_hub.application.dto.servicematerials.ServiceMaterialResponse;
import com.fiap.mechanical_hub.domain.entities.ServiceData;
import com.fiap.mechanical_hub.domain.entities.ServiceMaterial;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ServiceMapper {

    private ServiceMapper() {}

    public static CreateServiceCommand toCreateCommand(UpsertServiceRequest request) {
        return new CreateServiceCommand(
                request.getName(),
                request.getDescription(),
                request.getLaborCost(),
                request.getBasePrice(),
                request.getMaterials()
        );
    }

    public static UpdateServiceCommand toUpdateCommand(UUID id, UpsertServiceRequest request) {
        return new UpdateServiceCommand(
                id,
                request.getName(),
                request.getDescription(),
                request.getLaborCost(),
                request.getBasePrice(),
                request.getMaterials()
        );
    }

    public static ServiceResponse toResponse(ServiceData serviceData) {
        return new ServiceResponse(
                serviceData.getId(),
                serviceData.getName(),
                serviceData.getDescription(),
                serviceData.getLaborCost(),
                serviceData.getBasePrice(),
                serviceData.getTotalPrice(),
                serviceData.getMaterials().stream().map(ServiceMapper::toMaterialResponse).toList(),
                serviceData.isActive(),
                serviceData.getCreatedAt(),
                serviceData.getUpdatedAt()
        );
    }

    private static ServiceMaterialResponse toMaterialResponse(ServiceMaterial material) {
        return new ServiceMaterialResponse(
                material.getMaterial().getId(),
                material.getMaterial().getName(),
                material.getMaterial().getDescription(),
                material.getMaterial().getUnitPrice(),
                material.getQuantity()
        );
    }
}



