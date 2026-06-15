package com.fiap.mechanical_hub.domain.entities;

import com.fiap.mechanical_hub.domain.enums.StockMovementTypeEnum;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class StockMovementTest {

    private static final UUID MATERIAL_ID = UUID.fromString("00000000-0000-0000-0000-000000000020");
    private static final UUID SERVICE_ORDER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Test
    void shouldBuildStockEntryMovement_withCorrectType() {
        StockMovement movement = StockMovement.buildStockEntryMovement(MATERIAL_ID, 10);

        assertThat(movement.getId()).isNotNull();
        assertThat(movement.getMaterialId()).isEqualTo(MATERIAL_ID);
        assertThat(movement.getQuantity()).isEqualTo(10);
        assertThat(movement.getMovementType()).isEqualTo(StockMovementTypeEnum.ENTRY.getDescription());
        assertThat(movement.getServiceOrderId()).isNull();
    }

    @Test
    void shouldRegisterReservation_withServiceOrderId() {
        StockMovement movement = StockMovement.registerReservation(MATERIAL_ID, SERVICE_ORDER_ID, 5);

        assertThat(movement.getId()).isNotNull();
        assertThat(movement.getMaterialId()).isEqualTo(MATERIAL_ID);
        assertThat(movement.getServiceOrderId()).isEqualTo(SERVICE_ORDER_ID);
        assertThat(movement.getQuantity()).isEqualTo(5);
        assertThat(movement.getMovementType()).isEqualTo(StockMovementTypeEnum.RESERVED.getDescription());
    }

    @Test
    void shouldRegisterReturn_withCorrectType() {
        StockMovement movement = StockMovement.registerReturn(MATERIAL_ID, SERVICE_ORDER_ID, 3);

        assertThat(movement.getId()).isNotNull();
        assertThat(movement.getMaterialId()).isEqualTo(MATERIAL_ID);
        assertThat(movement.getServiceOrderId()).isEqualTo(SERVICE_ORDER_ID);
        assertThat(movement.getQuantity()).isEqualTo(3);
        assertThat(movement.getMovementType()).isEqualTo(StockMovementTypeEnum.RETURN.getDescription());
    }

    @Test
    void shouldRegisterDelete_withCorrectType() {
        StockMovement movement = StockMovement.registerDelete(MATERIAL_ID, SERVICE_ORDER_ID, 2);

        assertThat(movement.getId()).isNotNull();
        assertThat(movement.getMaterialId()).isEqualTo(MATERIAL_ID);
        assertThat(movement.getServiceOrderId()).isEqualTo(SERVICE_ORDER_ID);
        assertThat(movement.getQuantity()).isEqualTo(2);
        assertThat(movement.getMovementType()).isEqualTo(StockMovementTypeEnum.EXCLUDED.getDescription());
    }

    @Test
    void shouldRegisterStockOut_withCorrectType() {
        StockMovement movement = StockMovement.registerStockOut(MATERIAL_ID, SERVICE_ORDER_ID, 7);

        assertThat(movement.getId()).isNotNull();
        assertThat(movement.getMaterialId()).isEqualTo(MATERIAL_ID);
        assertThat(movement.getServiceOrderId()).isEqualTo(SERVICE_ORDER_ID);
        assertThat(movement.getQuantity()).isEqualTo(7);
        assertThat(movement.getMovementType()).isEqualTo(StockMovementTypeEnum.OUT.getDescription());
    }

    @Test
    void shouldRecordCreatedAt_forAllMovements() {
        StockMovement movement = StockMovement.buildStockEntryMovement(MATERIAL_ID, 10);

        assertThat(movement.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldMaintainUniqueIdPerMovement() {
        StockMovement movement1 = StockMovement.buildStockEntryMovement(MATERIAL_ID, 10);
        StockMovement movement2 = StockMovement.buildStockEntryMovement(MATERIAL_ID, 10);

        assertThat(movement1.getId()).isNotEqualTo(movement2.getId());
    }
}

