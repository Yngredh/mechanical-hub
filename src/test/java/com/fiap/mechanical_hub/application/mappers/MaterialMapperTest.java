package com.fiap.mechanical_hub.application.mappers;

import com.fiap.mechanical_hub.application.command.material.CreateMaterialCommand;
import com.fiap.mechanical_hub.application.command.material.UpdateMaterialCommand;
import com.fiap.mechanical_hub.application.dto.material.InsertMaterialRequest;
import com.fiap.mechanical_hub.application.dto.material.MaterialResponse;
import com.fiap.mechanical_hub.application.dto.material.UpdateMaterialRequest;
import com.fiap.mechanical_hub.domain.entities.Material;
import com.fiap.mechanical_hub.mocks.domain.entities.MaterialMock;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MaterialMapperTest {

    private static final UUID MATERIAL_ID = UUID.fromString("00000000-0000-0000-0000-000000000020");

    private final MaterialMapper mapper = new MaterialMapper();

    @Test
    void shouldMapAllFields_whenConvertingToResponse() {
        Material material = MaterialMock.withSufficientStock();

        MaterialResponse response = MaterialMapper.toResponse(material);

        assertThat(response.id()).isEqualTo(material.getId());
        assertThat(response.name()).isEqualTo(material.getName());
        assertThat(response.description()).isEqualTo(material.getDescription());
        assertThat(response.unitPrice()).isEqualTo(material.getUnitPrice());
        assertThat(response.minStockQuantity()).isEqualTo(material.getMinStockQuantity());
        assertThat(response.createdAt()).isEqualTo(material.getCreatedAt());
        assertThat(response.updatedAt()).isEqualTo(material.getUpdatedAt());
    }

    @Test
    void shouldMapAllFields_whenConvertingInsertRequestToCreateCommand() {
        InsertMaterialRequest request = new InsertMaterialRequest(
                "Óleo de motor", "Óleo 5W30", BigDecimal.valueOf(45.00), 10
        );

        CreateMaterialCommand command = mapper.toCreateCommand(request);

        assertThat(command.name()).isEqualTo(request.name());
        assertThat(command.description()).isEqualTo(request.description());
        assertThat(command.unitPrice()).isEqualTo(request.unitPrice());
        assertThat(command.minStockQuantity()).isEqualTo(request.minStockQuantity());
    }

    @Test
    void shouldMapAllFields_whenConvertingUpdateRequestToUpdateCommand() {
        UpdateMaterialRequest request = new UpdateMaterialRequest(
                "Filtro de ar", "Filtro atualizado", BigDecimal.valueOf(30.00), 5
        );

        UpdateMaterialCommand command = mapper.toUpdateCommand(MATERIAL_ID, request);

        assertThat(command.id()).isEqualTo(MATERIAL_ID);
        assertThat(command.name()).isEqualTo(request.name());
        assertThat(command.description()).isEqualTo(request.description());
        assertThat(command.unitPrice()).isEqualTo(request.unitPrice());
        assertThat(command.minStockQuantity()).isEqualTo(request.minStockQuantity());
    }
}
