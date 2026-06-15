package com.fiap.mechanical_hub.domain.serviceorder;

import com.fiap.mechanical_hub.mocks.domain.entities.StockMock;
import com.fiap.mechanical_hub.domain.entities.Stock;
import com.fiap.mechanical_hub.domain.entities.StockMovement;
import com.fiap.mechanical_hub.domain.enums.StockMovementTypeEnum;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AddServiceToOrderTest {

    private static final UUID MATERIAL_ID = UUID.fromString("00000000-0000-0000-0000-000000000020");
    private static final UUID SERVICE_ORDER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Test
    void shouldDeductStock_whenMaterialIsAvailable() {
        Stock stock = StockMock.available(20);

        stock.subtractQuantity(5);

        assertThat(stock.getQuantity()).isEqualTo(15);
    }

    @Test
    void shouldReserveStock_byCreatingReservedStockRecord() {
        Stock reservedStock = Stock.createReservedStock(MATERIAL_ID, 5);

        assertThat(reservedStock.getQuantity()).isEqualTo(5);
        assertThat(reservedStock.getMaterialId()).isEqualTo(MATERIAL_ID);
    }

    @Test
    void shouldCreateStockMovementOfTypeReserved_whenStockIsDeducted() {
        StockMovement movement = StockMovement.registerReservation(MATERIAL_ID, SERVICE_ORDER_ID, 5);

        assertThat(movement.getMovementType()).isEqualTo(StockMovementTypeEnum.RESERVED.getDescription());
        assertThat(movement.getQuantity()).isEqualTo(5);
        assertThat(movement.getServiceOrderId()).isEqualTo(SERVICE_ORDER_ID);
    }

    @Test
    void shouldRecordMaterialIdAndQuantityInReservation() {
        StockMovement movement = StockMovement.registerReservation(MATERIAL_ID, SERVICE_ORDER_ID, 10);

        assertThat(movement.getMaterialId()).isEqualTo(MATERIAL_ID);
        assertThat(movement.getQuantity()).isEqualTo(10);
    }

    @Test
    void shouldRegisterMultipleReservations_forMultipleMaterials() {
        UUID material1 = UUID.fromString("00000000-0000-0000-0000-000000000021");
        UUID material2 = UUID.fromString("00000000-0000-0000-0000-000000000022");

        StockMovement movement1 = StockMovement.registerReservation(material1, SERVICE_ORDER_ID, 5);
        StockMovement movement2 = StockMovement.registerReservation(material2, SERVICE_ORDER_ID, 3);

        assertThat(movement1.getMaterialId()).isEqualTo(material1);
        assertThat(movement2.getMaterialId()).isEqualTo(material2);
        assertThat(movement1.getId()).isNotEqualTo(movement2.getId());
    }

    @Test
    void shouldMaintainQuantity_inReservationMovement() {
        Integer reservedQuantity = 8;
        StockMovement movement = StockMovement.registerReservation(MATERIAL_ID, SERVICE_ORDER_ID, reservedQuantity);

        assertThat(movement.getQuantity()).isEqualTo(reservedQuantity);
    }
}

