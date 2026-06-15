package com.fiap.mechanical_hub.application.usecases.stock;

import com.fiap.mechanical_hub.domain.entities.StockPendingItem;
import com.fiap.mechanical_hub.domain.enums.StockStatusEnum;
import com.fiap.mechanical_hub.domain.repositories.ServiceOrderRepository;
import com.fiap.mechanical_hub.domain.repositories.StockMovementRepository;
import com.fiap.mechanical_hub.domain.repositories.StockPendingItemRepository;
import com.fiap.mechanical_hub.domain.repositories.StockRepository;
import com.fiap.mechanical_hub.mocks.domain.entities.ServiceOrderMock;
import com.fiap.mechanical_hub.mocks.domain.entities.StockMock;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ResolveMaterialPendingItemsUseCaseTest {

    private static final UUID MATERIAL_ID = UUID.fromString("00000000-0000-0000-0000-000000000020");
    private static final UUID ORDER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    private final StockPendingItemRepository stockPendingItemRepository = mock(StockPendingItemRepository.class);
    private final StockRepository stockRepository = mock(StockRepository.class);
    private final StockMovementRepository stockMovementRepository = mock(StockMovementRepository.class);
    private final ServiceOrderRepository serviceOrderRepository = mock(ServiceOrderRepository.class);
    private final ResolveMaterialPendingItemsUseCase useCase = new ResolveMaterialPendingItemsUseCase(
            stockPendingItemRepository, stockRepository, stockMovementRepository, serviceOrderRepository);

    @Test
    void shouldResolvePendingItemAndSaveReservation_whenSufficientStockAvailable() {
        StockPendingItem pending = new StockPendingItem(UUID.randomUUID(), ORDER_ID, MATERIAL_ID, 3, LocalDateTime.now());
        when(stockPendingItemRepository.findByMaterialIdOrderByCreatedAtAsc(MATERIAL_ID)).thenReturn(List.of(pending));
        when(stockRepository.findByMaterialIdAndStatus(MATERIAL_ID, StockStatusEnum.RESERVED)).thenReturn(Optional.empty());
        when(stockRepository.save(any())).thenReturn(StockMock.available(7));
        when(serviceOrderRepository.findById(ORDER_ID)).thenReturn(Optional.of(ServiceOrderMock.inDiagnosis()));

        useCase.execute(MATERIAL_ID, StockMock.available(10));

        verify(stockMovementRepository).save(any());
        verify(stockPendingItemRepository).delete(pending);
    }

    @Test
    void shouldNotResolvePending_whenNoStockAvailable() {
        StockPendingItem pending = new StockPendingItem(UUID.randomUUID(), ORDER_ID, MATERIAL_ID, 5, LocalDateTime.now());
        when(stockPendingItemRepository.findByMaterialIdOrderByCreatedAtAsc(MATERIAL_ID)).thenReturn(List.of(pending));

        useCase.execute(MATERIAL_ID, StockMock.empty());

        verify(stockPendingItemRepository, never()).delete(any());
        verify(stockMovementRepository, never()).save(any());
    }

    @Test
    void shouldReturnUpdatedStock_whenNoPendingItemsExist() {
        when(stockPendingItemRepository.findByMaterialIdOrderByCreatedAtAsc(MATERIAL_ID)).thenReturn(List.of());

        var result = useCase.execute(MATERIAL_ID, StockMock.available(10));

        assertThat(result).isNotNull();
    }
}
