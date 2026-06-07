package com.fiap.mechanical_hub.application.usecases.stock;

import com.fiap.mechanical_hub.domain.entities.StockMovement;
import com.fiap.mechanical_hub.domain.repositories.StockMovementRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FindStockMovementsByMaterialIdTest {

    private static final UUID MATERIAL_ID = UUID.fromString("00000000-0000-0000-0000-000000000020");

    private final StockMovementRepository stockMovementRepository = mock(StockMovementRepository.class);
    private final FindStockMovementsByMaterialId useCase = new FindStockMovementsByMaterialId(stockMovementRepository);

    @Test
    void shouldReturnMovements_whenMovementsExistForMaterial() {
        when(stockMovementRepository.findByMaterialId(MATERIAL_ID)).thenReturn(List.of(mock(StockMovement.class)));

        List<StockMovement> result = useCase.execute(MATERIAL_ID);

        assertThat(result).hasSize(1);
    }

    @Test
    void shouldReturnEmptyList_whenNoMovementsExistForMaterial() {
        when(stockMovementRepository.findByMaterialId(MATERIAL_ID)).thenReturn(List.of());

        List<StockMovement> result = useCase.execute(MATERIAL_ID);

        assertThat(result).isEmpty();
    }
}
