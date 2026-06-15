package com.fiap.mechanical_hub.domain.entities;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class StockPendingItemTest {

    private static final UUID SERVICE_ORDER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID MATERIAL_ID = UUID.fromString("00000000-0000-0000-0000-000000000020");

    @Test
    void shouldCreateStockPendingItem_withValidData() {
        StockPendingItem pendingItem = StockPendingItem.create(SERVICE_ORDER_ID, 5, MATERIAL_ID);

        assertThat(pendingItem.getId()).isNotNull();
        assertThat(pendingItem.getServiceOrderId()).isEqualTo(SERVICE_ORDER_ID);
        assertThat(pendingItem.getMaterialId()).isEqualTo(MATERIAL_ID);
        assertThat(pendingItem.getQuantity()).isEqualTo(5);
    }

    @Test
    void shouldRecordCreatedAt_whenCreatingPendingItem() {
        StockPendingItem pendingItem = StockPendingItem.create(SERVICE_ORDER_ID, 10, MATERIAL_ID);

        assertThat(pendingItem.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldMaintainUniqueIdPerPendingItem() {
        StockPendingItem pendingItem1 = StockPendingItem.create(SERVICE_ORDER_ID, 5, MATERIAL_ID);
        StockPendingItem pendingItem2 = StockPendingItem.create(SERVICE_ORDER_ID, 5, MATERIAL_ID);

        assertThat(pendingItem1.getId()).isNotEqualTo(pendingItem2.getId());
    }

    @Test
    void shouldStoreAllRequiredData_inPendingItem() {
        Integer requiredQuantity = 15;
        StockPendingItem pendingItem = StockPendingItem.create(SERVICE_ORDER_ID, requiredQuantity, MATERIAL_ID);

        assertThat(pendingItem.getServiceOrderId()).isEqualTo(SERVICE_ORDER_ID);
        assertThat(pendingItem.getMaterialId()).isEqualTo(MATERIAL_ID);
        assertThat(pendingItem.getQuantity()).isEqualTo(requiredQuantity);
    }

    @Test
    void shouldCreateMultiplePendingItems_forDifferentMaterials() {
        UUID materialId1 = UUID.fromString("00000000-0000-0000-0000-000000000021");
        UUID materialId2 = UUID.fromString("00000000-0000-0000-0000-000000000022");

        StockPendingItem pendingItem1 = StockPendingItem.create(SERVICE_ORDER_ID, 5, materialId1);
        StockPendingItem pendingItem2 = StockPendingItem.create(SERVICE_ORDER_ID, 3, materialId2);

        assertThat(pendingItem1.getMaterialId()).isEqualTo(materialId1);
        assertThat(pendingItem2.getMaterialId()).isEqualTo(materialId2);
        assertThat(pendingItem1.getId()).isNotEqualTo(pendingItem2.getId());
    }
}

