package com.fiap.mechanical_hub.application.usecases.stock;

import com.fiap.mechanical_hub.application.command.stock.RegisterStockEntryCommand;
import com.fiap.mechanical_hub.domain.entities.Stock;
import com.fiap.mechanical_hub.domain.enums.StockStatusEnum;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.domain.repositories.StockMovementRepository;
import com.fiap.mechanical_hub.domain.repositories.StockRepository;
import com.fiap.mechanical_hub.mocks.domain.entities.StockMock;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RegisterStockEntryUseCaseTest {

    private static final UUID MATERIAL_ID = UUID.fromString("00000000-0000-0000-0000-000000000020");

    private final StockRepository stockRepository = mock(StockRepository.class);
    private final StockMovementRepository stockMovementRepository = mock(StockMovementRepository.class);
    private final ResolveMaterialPendingItemsUseCase resolveMaterialPendingItemsUseCase = mock(ResolveMaterialPendingItemsUseCase.class);
    private final RegisterStockEntryUseCase useCase = new RegisterStockEntryUseCase(stockRepository, stockMovementRepository, resolveMaterialPendingItemsUseCase);

    @Test
    void shouldIncreaseStockAndSaveMovement_whenStockExists() {
        Stock stock = StockMock.available(10);
        when(stockRepository.findByMaterialIdAndStatus(MATERIAL_ID, StockStatusEnum.AVAILABLE)).thenReturn(Optional.of(stock));
        when(stockRepository.save(any())).thenReturn(stock);
        when(resolveMaterialPendingItemsUseCase.execute(any(), any())).thenReturn(stock);

        Stock result = useCase.execute(new RegisterStockEntryCommand(MATERIAL_ID, 5));

        assertThat(result).isNotNull();
        verify(stockMovementRepository).save(any());
    }

    @Test
    void shouldThrowNotFoundException_whenStockNotFound() {
        when(stockRepository.findByMaterialIdAndStatus(MATERIAL_ID, StockStatusEnum.AVAILABLE)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(new RegisterStockEntryCommand(MATERIAL_ID, 5)))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(MATERIAL_ID.toString());
    }

    @Test
    void shouldResolvePendingItems_afterRegisteringEntry() {
        Stock stock = StockMock.available(10);
        when(stockRepository.findByMaterialIdAndStatus(MATERIAL_ID, StockStatusEnum.AVAILABLE)).thenReturn(Optional.of(stock));
        when(stockRepository.save(any())).thenReturn(stock);
        when(resolveMaterialPendingItemsUseCase.execute(any(), any())).thenReturn(stock);

        useCase.execute(new RegisterStockEntryCommand(MATERIAL_ID, 5));

        verify(resolveMaterialPendingItemsUseCase).execute(any(), any());
    }
}
