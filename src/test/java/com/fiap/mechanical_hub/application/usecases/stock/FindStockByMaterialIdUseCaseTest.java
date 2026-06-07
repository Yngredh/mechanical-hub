package com.fiap.mechanical_hub.application.usecases.stock;

import com.fiap.mechanical_hub.domain.entities.Stock;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.domain.repositories.StockMovementRepository;
import com.fiap.mechanical_hub.domain.repositories.StockRepository;
import com.fiap.mechanical_hub.mocks.domain.entities.StockMock;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FindStockByMaterialIdUseCaseTest {

    private static final UUID MATERIAL_ID = UUID.fromString("00000000-0000-0000-0000-000000000020");

    private final StockRepository stockRepository = mock(StockRepository.class);
    private final StockMovementRepository stockMovementRepository = mock(StockMovementRepository.class);
    private final FindStockByMaterialIdUseCase useCase = new FindStockByMaterialIdUseCase(stockRepository, stockMovementRepository);

    @Test
    void shouldReturnStocks_whenStocksExistForMaterial() {
        // Arrange
        when(stockRepository.findAllByMaterialId(MATERIAL_ID)).thenReturn(List.of(StockMock.available(10)));

        // Act
        List<Stock> result = useCase.execute(MATERIAL_ID);

        // Assert
        assertThat(result).hasSize(1);
    }

    @Test
    void shouldThrowNotFoundException_whenNoStocksExistForMaterial() {
        // Arrange
        when(stockRepository.findAllByMaterialId(MATERIAL_ID)).thenReturn(List.of());

        // Act & Assert
        assertThatThrownBy(() -> useCase.execute(MATERIAL_ID))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(MATERIAL_ID.toString());
    }
}
