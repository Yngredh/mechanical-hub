package com.fiap.mechanical_hub.application.usecases.stock;

import com.fiap.mechanical_hub.domain.entities.Stock;
import com.fiap.mechanical_hub.domain.enums.StockStatusEnum;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.domain.repositories.StockMovementRepository;
import com.fiap.mechanical_hub.domain.repositories.StockRepository;
import com.fiap.mechanical_hub.mocks.domain.entities.OrderTaskMock;
import com.fiap.mechanical_hub.mocks.domain.entities.StockMock;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RestoreReservedStockItemsUseCaseTest {

    private static final UUID ORDER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    private final StockRepository stockRepository = mock(StockRepository.class);
    private final StockMovementRepository stockMovementRepository = mock(StockMovementRepository.class);
    private final RestoreReservedStockItemsUseCase useCase = new RestoreReservedStockItemsUseCase(stockRepository, stockMovementRepository);

    @Test
    void shouldRestoreStockAndSaveMovement_whenOrderHasTasks() {
        Stock reserved = StockMock.reserved(10);
        Stock available = StockMock.available(5);
        when(stockRepository.findByMaterialIdAndStatus(any(), eq(StockStatusEnum.RESERVED))).thenReturn(Optional.of(reserved));
        when(stockRepository.findByMaterialIdAndStatus(any(), eq(StockStatusEnum.AVAILABLE))).thenReturn(Optional.of(available));
        when(stockRepository.save(any())).thenReturn(available);

        useCase.execute(ORDER_ID, List.of(OrderTaskMock.finished()));

        verify(stockMovementRepository).save(any());
        verify(stockRepository).save(any());
    }

    @Test
    void shouldThrowNotFoundException_whenReservedStockNotFound() {
        when(stockRepository.findByMaterialIdAndStatus(any(), eq(StockStatusEnum.RESERVED))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(ORDER_ID, List.of(OrderTaskMock.finished())))
                .isInstanceOf(NotFoundException.class);
    }
}
