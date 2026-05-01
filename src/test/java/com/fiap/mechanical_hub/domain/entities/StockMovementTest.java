package com.fiap.mechanical_hub.domain.entities;

import com.fiap.mechanical_hub.domain.entities.mocks.StockMovementMock;
import com.fiap.mechanical_hub.domain.entities.constants.TestConstants;
import com.fiap.mechanical_hub.domain.enums.StockMovementTypeEnum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StockMovementTest {

    @Test
    void shouldCreateEmptyStockMovement() {
        StockMovement movement = new StockMovement();

        assertAll(
                () -> assertNull(movement.getId()),
                () -> assertNull(movement.getMaterialId()),
                () -> assertNull(movement.getServiceOrderId()),
                () -> assertNull(movement.getMovementType()),
                () -> assertNull(movement.getQuantity()),
                () -> assertNull(movement.getCreatedAt())
        );
    }

    @Test
    void shouldCreateEntryMovement() {
        StockMovement movement = StockMovementMock.defaultEntryMovement();

        assertNotNull(movement.getId());
        assertEquals(TestConstants.DEFAULT_MATERIAL_ID, movement.getMaterialId());
        assertNull(movement.getServiceOrderId());
        assertEquals(StockMovementTypeEnum.ENTRY.getDescription(), movement.getMovementType());
        assertNotNull(movement.getCreatedAt());
    }

    @Test
    void shouldCreateReservedMovement() {
        StockMovement movement = StockMovementMock.reservedMovement();

        assertNotNull(movement.getId());
        assertEquals(TestConstants.DEFAULT_MATERIAL_ID, movement.getMaterialId());
        assertEquals(TestConstants.DEFAULT_SERVICE_ORDER_ID, movement.getServiceOrderId());
        assertEquals(StockMovementTypeEnum.RESERVED.getDescription(), movement.getMovementType());
    }

    @Test
    void shouldCreateReturnMovement() {
        StockMovement movement = StockMovementMock.returnMovement();

        assertNotNull(movement.getId());
        assertEquals(TestConstants.DEFAULT_MATERIAL_ID, movement.getMaterialId());
        assertEquals(TestConstants.DEFAULT_SERVICE_ORDER_ID, movement.getServiceOrderId());
        assertEquals(StockMovementTypeEnum.RETURN.getDescription(), movement.getMovementType());
    }

    @Test
    void shouldCreateEntryMovementWithCustomQuantity() {
        Integer customQuantity = 50;
        StockMovement movement = StockMovementMock.entryMovementWithQuantity(customQuantity);

        assertEquals(customQuantity, movement.getQuantity());
        assertEquals(StockMovementTypeEnum.ENTRY.getDescription(), movement.getMovementType());
    }

    @Test
    void shouldHaveNullServiceOrderIdForEntryMovement() {
        StockMovement movement = StockMovementMock.defaultEntryMovement();

        assertNull(movement.getServiceOrderId());
    }

    @Test
    void shouldHaveServiceOrderIdForReservedMovement() {
        StockMovement movement = StockMovementMock.reservedMovement();

        assertNotNull(movement.getServiceOrderId());
        assertEquals(TestConstants.DEFAULT_SERVICE_ORDER_ID, movement.getServiceOrderId());
    }

    @Test
    void shouldHaveServiceOrderIdForReturnMovement() {
        StockMovement movement = StockMovementMock.returnMovement();

        assertNotNull(movement.getServiceOrderId());
        assertEquals(TestConstants.DEFAULT_SERVICE_ORDER_ID, movement.getServiceOrderId());
    }

    @Test
    void shouldHaveCorrectMovementTypeForEntry() {
        StockMovement movement = StockMovementMock.defaultEntryMovement();

        assertEquals(StockMovementTypeEnum.ENTRY.getDescription(), movement.getMovementType());
    }

    @Test
    void shouldHaveCorrectMovementTypeForReserved() {
        StockMovement movement = StockMovementMock.reservedMovement();

        assertEquals(StockMovementTypeEnum.RESERVED.getDescription(), movement.getMovementType());
    }

    @Test
    void shouldHaveCorrectMovementTypeForReturn() {
        StockMovement movement = StockMovementMock.returnMovement();

        assertEquals(StockMovementTypeEnum.RETURN.getDescription(), movement.getMovementType());
    }

}

