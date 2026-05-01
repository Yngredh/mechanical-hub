package com.fiap.mechanical_hub.domain.entities;

import com.fiap.mechanical_hub.domain.entities.mocks.StockPendingItemMock;
import com.fiap.mechanical_hub.domain.entities.constants.TestConstants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StockPendingItemTest {

    @Test
    void shouldCreateEmptyStockPendingItem() {
        StockPendingItem pendingItem = new StockPendingItem();

        assertAll(
                () -> assertNull(pendingItem.getId()),
                () -> assertNull(pendingItem.getServiceOrderId()),
                () -> assertNull(pendingItem.getMaterialId()),
                () -> assertNull(pendingItem.getQuantity()),
                () -> assertNull(pendingItem.getCreatedAt())
        );
    }

    @Test
    void shouldCreateStockPendingItemWithValidData() {
        StockPendingItem pendingItem = StockPendingItemMock.defaultPendingItem();

        assertNotNull(pendingItem.getId());
        assertEquals(TestConstants.DEFAULT_SERVICE_ORDER_ID, pendingItem.getServiceOrderId());
        assertEquals(TestConstants.DEFAULT_MATERIAL_ID, pendingItem.getMaterialId());
        assertEquals(5, pendingItem.getQuantity());
        assertNotNull(pendingItem.getCreatedAt());
    }

    @Test
    void shouldCreatePendingItemWithCustomQuantity() {
        Integer customQuantity = 20;
        StockPendingItem pendingItem = StockPendingItemMock.pendingItemWithQuantity(customQuantity);

        assertEquals(customQuantity, pendingItem.getQuantity());
        assertEquals(TestConstants.DEFAULT_SERVICE_ORDER_ID, pendingItem.getServiceOrderId());
        assertEquals(TestConstants.DEFAULT_MATERIAL_ID, pendingItem.getMaterialId());
    }

    @Test
    void shouldCreatePendingItemWithCustomValues() {
        java.util.UUID customOrderId = java.util.UUID.randomUUID();
        java.util.UUID customMaterialId = java.util.UUID.randomUUID();
        Integer customQuantity = 15;

        StockPendingItem pendingItem = StockPendingItemMock.pendingItemWithCustomValues(
                customOrderId,
                customQuantity,
                customMaterialId
        );

        assertEquals(customOrderId, pendingItem.getServiceOrderId());
        assertEquals(customMaterialId, pendingItem.getMaterialId());
        assertEquals(customQuantity, pendingItem.getQuantity());
    }

    @Test
    void shouldHaveUniqueIds() {
        StockPendingItem item1 = StockPendingItemMock.defaultPendingItem();
        StockPendingItem item2 = StockPendingItemMock.defaultPendingItem();

        assertNotEquals(item1.getId(), item2.getId());
    }

    @Test
    void shouldMaintainQuantity() {
        Integer quantity = 10;
        StockPendingItem pendingItem = StockPendingItemMock.pendingItemWithQuantity(quantity);

        assertEquals(quantity, pendingItem.getQuantity());
    }

    @Test
    void shouldHaveCreatedAtTimestamp() {
        StockPendingItem pendingItem = StockPendingItemMock.defaultPendingItem();

        assertNotNull(pendingItem.getCreatedAt());
        assertTrue(pendingItem.getCreatedAt().isBefore(java.time.LocalDateTime.now().plusSeconds(1)));
    }

}

