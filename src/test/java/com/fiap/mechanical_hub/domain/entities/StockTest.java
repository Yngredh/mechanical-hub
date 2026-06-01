package com.fiap.mechanical_hub.domain.entities;

import com.fiap.mechanical_hub.mocks.domain.entities.StockMock;
import com.fiap.mechanical_hub.domain.exceptions.BusinessRuleException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StockTest {

    @Test
    void shouldAddQuantity_whenQuantityIsPositive() {
        Stock stock = StockMock.available(10);

        stock.addQuantity(5);

        assertThat(stock.getQuantity()).isEqualTo(15);
        assertThat(stock.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldThrowException_whenAddingNegativeQuantity() {
        Stock stock = StockMock.available(10);

        assertThatThrownBy(() -> stock.addQuantity(-5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("negativa");
    }

    @Test
    void shouldSubtractQuantity_whenSufficientQuantityAvailable() {
        Stock stock = StockMock.available(10);

        stock.subtractQuantity(3);

        assertThat(stock.getQuantity()).isEqualTo(7);
        assertThat(stock.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldThrowException_whenSubtractingMoreThanAvailable() {
        Stock stock = StockMock.available(10);

        assertThatThrownBy(() -> stock.subtractQuantity(15))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("insuficiente");
    }

    @Test
    void shouldReturnTrue_whenMaterialIsNotAvailable() {
        Stock stock = StockMock.available(5);

        assertThat(stock.checkMaterialAvailability(10)).isTrue();
    }

    @Test
    void shouldReturnFalse_whenMaterialIsAvailable() {
        Stock stock = StockMock.available(20);

        assertThat(stock.checkMaterialAvailability(10)).isFalse();
    }

    @Test
    void shouldReleaseReservedQuantity_whenQuantityIsSufficient() {
        Stock stock = StockMock.reserved(10);

        stock.release(5);

        assertThat(stock.getQuantity()).isEqualTo(5);
    }

    @Test
    void shouldThrowException_whenReleasingMoreThanReserved() {
        Stock stock = StockMock.reserved(10);

        assertThatThrownBy(() -> stock.release(15))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void shouldReplenishStock_addingQuantity() {
        Stock stock = StockMock.available(10);

        stock.replenish(5);

        assertThat(stock.getQuantity()).isEqualTo(15);
    }

    @Test
    void shouldDecreaseReservedStock_whenQuantityIsSufficient() {
        Stock stock = StockMock.reserved(10);

        stock.decreaseReserved(3);

        assertThat(stock.getQuantity()).isEqualTo(7);
        assertThat(stock.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldThrowException_whenDecreasingNonReservedStock() {
        Stock stock = StockMock.available(10);

        assertThatThrownBy(() -> stock.decreaseReserved(5))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("reservado");
    }

    @Test
    void shouldThrowException_whenDecreasingMoreThanReserved() {
        Stock stock = StockMock.reserved(10);

        assertThatThrownBy(() -> stock.decreaseReserved(15))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("insuficiente");
    }
}

