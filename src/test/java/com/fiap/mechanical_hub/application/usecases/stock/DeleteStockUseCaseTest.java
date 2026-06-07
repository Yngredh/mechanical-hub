package com.fiap.mechanical_hub.application.usecases.stock;

import com.fiap.mechanical_hub.application.command.stock.DeleteStockCommand;
import com.fiap.mechanical_hub.domain.exceptions.BusinessRuleException;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.domain.repositories.MaterialRepository;
import com.fiap.mechanical_hub.domain.repositories.StockMovementRepository;
import com.fiap.mechanical_hub.domain.repositories.StockRepository;
import com.fiap.mechanical_hub.mocks.domain.entities.MaterialMock;
import com.fiap.mechanical_hub.mocks.domain.entities.StockMock;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DeleteStockUseCaseTest {

    private static final UUID MATERIAL_ID = UUID.fromString("00000000-0000-0000-0000-000000000020");

    private final StockRepository stockRepository = mock(StockRepository.class);
    private final StockMovementRepository stockMovementRepository = mock(StockMovementRepository.class);
    private final MaterialRepository materialRepository = mock(MaterialRepository.class);
    private final DeleteStockUseCase useCase = new DeleteStockUseCase(stockRepository, stockMovementRepository, materialRepository);

    @Test
    void shouldDeleteStockAndMaterial_whenNoReservedStock() {
        when(materialRepository.findById(MATERIAL_ID)).thenReturn(Optional.of(MaterialMock.withSufficientStock()));
        when(stockRepository.findAllByMaterialId(MATERIAL_ID)).thenReturn(List.of(StockMock.available(10)));
        when(stockRepository.findByMaterialIdAndStatus(any(), any())).thenReturn(Optional.of(StockMock.available(10)));

        useCase.execute(new DeleteStockCommand(MATERIAL_ID));

        verify(stockRepository).deleteByMaterialId(MATERIAL_ID);
        verify(materialRepository).deleteById(MATERIAL_ID);
    }

    @Test
    void shouldThrowNotFoundException_whenMaterialDoesNotExist() {
        when(materialRepository.findById(MATERIAL_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(new DeleteStockCommand(MATERIAL_ID)))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(MATERIAL_ID.toString());
    }

    @Test
    void shouldThrowBusinessRuleException_whenMaterialHasReservedStock() {
        when(materialRepository.findById(MATERIAL_ID)).thenReturn(Optional.of(MaterialMock.withSufficientStock()));
        when(stockRepository.findAllByMaterialId(MATERIAL_ID)).thenReturn(List.of(StockMock.available(10), StockMock.reserved(5)));

        assertThatThrownBy(() -> useCase.execute(new DeleteStockCommand(MATERIAL_ID)))
                .isInstanceOf(BusinessRuleException.class);
    }
}
