package com.fiap.mechanical_hub.application.usecases.stock;

import com.fiap.mechanical_hub.domain.entities.Stock;
import com.fiap.mechanical_hub.domain.enums.StockStatusEnum;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.domain.repositories.StockMovementRepository;
import com.fiap.mechanical_hub.domain.repositories.StockRepository;
import com.fiap.mechanical_hub.mocks.domain.entities.OrderTaskMock;
import com.fiap.mechanical_hub.mocks.domain.entities.StockMock;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RegisterStockOutUseCaseTest {

    private static final UUID ORDER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID MATERIAL_ID = UUID.fromString("00000000-0000-0000-0000-000000000050");

    private final StockRepository stockRepository = mock(StockRepository.class);
    private final StockMovementRepository stockMovementRepository = mock(StockMovementRepository.class);
    private final RegisterStockOutUseCase useCase = new RegisterStockOutUseCase(stockRepository, stockMovementRepository);

    @Test
    void shouldDecreaseReservedStockAndSaveMovement_whenReservedStockExists() {
        Stock reserved = StockMock.reserved(10);
        when(stockRepository.findByMaterialIdAndStatus(any(), eq(StockStatusEnum.RESERVED))).thenReturn(Optional.of(reserved));
        when(stockRepository.save(any())).thenReturn(reserved);

        useCase.execute(ORDER_ID, OrderTaskMock.finished());

        verify(stockRepository).save(any());
        verify(stockMovementRepository).save(any());
    }

    @Test
    void shouldThrowNotFoundException_whenReservedStockNotFound() {
        when(stockRepository.findByMaterialIdAndStatus(any(), eq(StockStatusEnum.RESERVED))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(ORDER_ID, OrderTaskMock.finished())).isInstanceOf(NotFoundException.class);
    }
}
