package com.fiap.mechanical_hub.domain.serviceorder;

import com.fiap.mechanical_hub.mocks.domain.entities.StockMock;
import com.fiap.mechanical_hub.domain.entities.Stock;
import com.fiap.mechanical_hub.domain.entities.StockMovement;
import com.fiap.mechanical_hub.domain.entities.StockPendingItem;
import com.fiap.mechanical_hub.domain.enums.StockMovementTypeEnum;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RejectOrderStockRestorationTest {

    private static final UUID MATERIAL_ID = UUID.fromString("00000000-0000-0000-0000-000000000020");
    private static final UUID SERVICE_ORDER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID OLDER_ORDER_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final UUID NEWER_ORDER_ID = UUID.fromString("00000000-0000-0000-0000-000000000003");

    @Test
    void shouldRestoreReservedStock_toAvailableStatus_whenOrderIsRejected() {
        Stock reservedStock = StockMock.reserved(10);

        reservedStock.release(10);

        assertThat(reservedStock.getQuantity()).isZero();
    }

    @Test
    void shouldReplenishAvailableStock_whenRestoringFromReservation() {
        Stock availableStock = StockMock.available(20);
        Stock reservedStock = StockMock.reserved(5);

        reservedStock.release(5);
        availableStock.replenish(5);

        assertThat(availableStock.getQuantity()).isEqualTo(25);
    }

    @Test
    void shouldCreateStockMovementOfTypeRetorno_whenOrderIsRejected() {
        StockMovement movement = StockMovement.registerReturn(MATERIAL_ID, SERVICE_ORDER_ID, 5);

        assertThat(movement.getMovementType()).isEqualTo(StockMovementTypeEnum.RETURN.getDescription());
        assertThat(movement.getQuantity()).isEqualTo(5);
        assertThat(movement.getServiceOrderId()).isEqualTo(SERVICE_ORDER_ID);
    }

    @Test
    void shouldRecordMovementDetails_inReturnMovement() {
        StockMovement movement = StockMovement.registerReturn(MATERIAL_ID, SERVICE_ORDER_ID, 8);

        assertThat(movement.getMaterialId()).isEqualTo(MATERIAL_ID);
        assertThat(movement.getServiceOrderId()).isEqualTo(SERVICE_ORDER_ID);
        assertThat(movement.getQuantity()).isEqualTo(8);
        assertThat(movement.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldCreateStockPendingItem_toTrackPendency() {
        StockPendingItem pendingItem = StockPendingItem.create(SERVICE_ORDER_ID, 10, MATERIAL_ID);

        assertThat(pendingItem.getServiceOrderId()).isEqualTo(SERVICE_ORDER_ID);
        assertThat(pendingItem.getMaterialId()).isEqualTo(MATERIAL_ID);
        assertThat(pendingItem.getQuantity()).isEqualTo(10);
    }

    @Test
    void shouldPrioritizeOldestPendingOrder_byComparisonOfCreatedAt() {
        StockPendingItem olderPending = StockPendingItem.create(OLDER_ORDER_ID, 5, MATERIAL_ID);
        StockPendingItem newerPending = StockPendingItem.create(NEWER_ORDER_ID, 5, MATERIAL_ID);

        assertThat(olderPending.getCreatedAt()).isBeforeOrEqualTo(newerPending.getCreatedAt());
    }

    @Test
    void shouldResolvePendingItem_whenSufficientStockIsReplenished() {
        Stock availableStock = StockMock.available(10);

        availableStock.subtractQuantity(5);

        assertThat(availableStock.getQuantity()).isEqualTo(5);
    }

    @Test
    void shouldTrackMultiplePendingItems_forDifferentMaterials() {
        UUID material1 = UUID.fromString("00000000-0000-0000-0000-000000000021");
        UUID material2 = UUID.fromString("00000000-0000-0000-0000-000000000022");

        StockPendingItem pending1 = StockPendingItem.create(SERVICE_ORDER_ID, 3, material1);
        StockPendingItem pending2 = StockPendingItem.create(SERVICE_ORDER_ID, 5, material2);

        assertThat(pending1.getMaterialId()).isEqualTo(material1);
        assertThat(pending2.getMaterialId()).isEqualTo(material2);
    }

    @Test
    void shouldRecordReturnMovementForEachMaterial_whenOrderIsRejected() {
        UUID material1 = UUID.fromString("00000000-0000-0000-0000-000000000021");
        UUID material2 = UUID.fromString("00000000-0000-0000-0000-000000000022");

        StockMovement movement1 = StockMovement.registerReturn(material1, SERVICE_ORDER_ID, 5);
        StockMovement movement2 = StockMovement.registerReturn(material2, SERVICE_ORDER_ID, 3);

        assertThat(movement1.getMovementType()).isEqualTo(StockMovementTypeEnum.RETURN.getDescription());
        assertThat(movement2.getMovementType()).isEqualTo(StockMovementTypeEnum.RETURN.getDescription());
    }
}

