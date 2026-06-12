package com.fiap.mechanical_hub.infrastructure.database.mappers;

import com.fiap.mechanical_hub.domain.entities.StockMovement;
import com.fiap.mechanical_hub.infrastructure.database.models.StockMovementModel;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class StockMovementRepositoryMapperTest {

    private static final UUID MOVEMENT_ID = UUID.fromString("00000000-0000-0000-0000-000000000080");
    private static final UUID MATERIAL_ID = UUID.fromString("00000000-0000-0000-0000-000000000020");
    private static final UUID SERVICE_ORDER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2024, 1, 1, 10, 0);

    private StockMovement buildDomain() {
        return new StockMovement(MOVEMENT_ID, MATERIAL_ID, SERVICE_ORDER_ID, "reserva", 5, CREATED_AT);
    }

    private StockMovementModel buildModel() {
        return new StockMovementModel(MOVEMENT_ID, MATERIAL_ID, SERVICE_ORDER_ID, "reserva", 5, CREATED_AT);
    }

    @Test
    void shouldMapAllFields_whenConvertingDomainToModel() {
        StockMovement movement = buildDomain();

        StockMovementModel model = StockMovementRepositoryMapper.toModel(movement);

        assertThat(model.getId()).isEqualTo(movement.getId());
        assertThat(model.getMaterialId()).isEqualTo(movement.getMaterialId());
        assertThat(model.getServiceOrderId()).isEqualTo(movement.getServiceOrderId());
        assertThat(model.getMovementType()).isEqualTo(movement.getMovementType());
        assertThat(model.getQuantity()).isEqualTo(movement.getQuantity());
        assertThat(model.getCreatedAt()).isEqualTo(movement.getCreatedAt());
    }

    @Test
    void shouldMapAllFields_whenConvertingModelToEntity() {
        StockMovementModel model = buildModel();

        StockMovement movement = StockMovementRepositoryMapper.toEntity(model);

        assertThat(movement.getId()).isEqualTo(model.getId());
        assertThat(movement.getMaterialId()).isEqualTo(model.getMaterialId());
        assertThat(movement.getServiceOrderId()).isEqualTo(model.getServiceOrderId());
        assertThat(movement.getMovementType()).isEqualTo(model.getMovementType());
        assertThat(movement.getQuantity()).isEqualTo(model.getQuantity());
        assertThat(movement.getCreatedAt()).isEqualTo(model.getCreatedAt());
    }
}
