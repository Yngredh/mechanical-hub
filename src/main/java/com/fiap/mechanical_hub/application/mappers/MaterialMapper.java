package com.fiap.mechanical_hub.infrastructure.http.mappers;

import com.fiap.mechanical_hub.application.command.material.CreateMaterialCommand;
import com.fiap.mechanical_hub.application.command.material.UpdateMaterialCommand;
import com.fiap.mechanical_hub.application.dto.material.InsertMaterialRequest;
import com.fiap.mechanical_hub.application.dto.material.MaterialResponse;
import com.fiap.mechanical_hub.application.dto.material.UpdateMaterialRequest;
import com.fiap.mechanical_hub.domain.entities.Material;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MaterialMapper {

    public static MaterialResponse toResponse(Material material) {
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

    public CreateMaterialCommand toCreateCommand(InsertMaterialRequest request) {
        return new CreateMaterialCommand(
                request.name(),
                request.description(),
                request.unitPrice(),
                request.minStockQuantity()
        );
    }

    public UpdateMaterialCommand toUpdateCommand(UUID id, UpdateMaterialRequest request) {
        return new UpdateMaterialCommand(
                id,
                request.name(),
                request.description(),
                request.unitPrice(),
                request.minStockQuantity()
        );
    }

}
