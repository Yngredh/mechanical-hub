package com.fiap.mechanical_hub.domain.entities;

import com.fiap.mechanical_hub.domain.entities.mocks.StockMock;
import com.fiap.mechanical_hub.domain.entities.constants.TestConstants;
import com.fiap.mechanical_hub.domain.enums.StockStatusEnum;
import com.fiap.mechanical_hub.domain.exceptions.BusinessRuleException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StockTest {

    @Test
    void shouldCreateAvailableStockForNewMaterial() {
        Stock stock = StockMock.defaultStock();

        assertNotNull(stock.getId());
        assertEquals(TestConstants.DEFAULT_MATERIAL_ID, stock.getMaterialId());
        assertEquals(0, stock.getQuantity());
        assertEquals(StockStatusEnum.AVAILABLE, stock.getStatus());
    }

    @Test
    void shouldCreateReservedStock() {
        Stock stock = StockMock.reservedStock();

        assertNotNull(stock.getId());
        assertEquals(TestConstants.DEFAULT_MATERIAL_ID, stock.getMaterialId());
        assertEquals(TestConstants.DEFAULT_STOCK_QUANTITY, stock.getQuantity());
        assertEquals(StockStatusEnum.RESERVED, stock.getStatus());
    }

    @Test
    void shouldAddQuantityToStock() {
        Stock stock = StockMock.availableStock();
        Integer initialQuantity = stock.getQuantity();
        Integer quantityToAdd = 20;

        stock.addQuantity(quantityToAdd);

        assertEquals(initialQuantity + quantityToAdd, stock.getQuantity());
    }

    @Test
    void shouldSubtractQuantityFromStock() {
        Stock stock = StockMock.availableStock();
        Integer initialQuantity = stock.getQuantity();
        Integer quantityToSubtract = 10;

        stock.subtractQuantity(quantityToSubtract);

        assertEquals(initialQuantity - quantityToSubtract, stock.getQuantity());
    }

    @Test
    void shouldThrowExceptionWhenAddingNegativeQuantity() {
        Stock stock = StockMock.defaultStock();

        assertThrows(IllegalArgumentException.class, () ->
            stock.addQuantity(-5)
        );
    }

    @Test
    void shouldThrowExceptionWhenSubtractingMoreThanAvailable() {
        Stock stock = StockMock.availableStock();

        assertThrows(IllegalArgumentException.class, () ->
            stock.subtractQuantity(TestConstants.DEFAULT_STOCK_QUANTITY + 10)
        );
    }

    @Test
    void shouldCheckMaterialAvailabilityWhenInsufficient() {
        Stock stock = StockMock.stockWithQuantity(5);

        assertTrue(stock.checkMaterialAvailability(10));
    }

    @Test
    void shouldCheckMaterialAvailabilityWhenSufficient() {
        Stock stock = StockMock.stockWithQuantity(20);

        assertFalse(stock.checkMaterialAvailability(10));
    }

    @Test
    void shouldReleaseQuantityFromReservedStock() {
        Stock stock = StockMock.reservedStock();
        Integer initialQuantity = stock.getQuantity();

        stock.release(10);

        assertEquals(initialQuantity - 10, stock.getQuantity());
    }

    @Test
    void shouldThrowExceptionWhenReleasingMoreThanReserved() {
        Stock stock = StockMock.reservedStock();

        assertThrows(BusinessRuleException.class, () ->
            stock.release(TestConstants.DEFAULT_STOCK_QUANTITY + 10)
        );
    }

    @Test
    void shouldReplenishStock() {
        Stock stock = StockMock.availableStock();
        Integer initialQuantity = stock.getQuantity();

        stock.replenish(25);

        assertEquals(initialQuantity + 25, stock.getQuantity());
    }

}

