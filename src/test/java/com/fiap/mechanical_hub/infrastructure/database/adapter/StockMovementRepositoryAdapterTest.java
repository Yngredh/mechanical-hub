package com.fiap.mechanical_hub.infrastructure.database.adapter;

import com.fiap.mechanical_hub.domain.entities.StockMovement;
import com.fiap.mechanical_hub.mocks.infrastructure.database.models.StockMovementModelMock;
import com.fiap.mechanical_hub.infrastructure.database.repositories.StockMovementJpaRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StockMovementRepositoryAdapterTest {

    private final StockMovementJpaRepository jpaRepository = mock(StockMovementJpaRepository.class);

    private final StockMovementRepositoryAdapter adapter = new StockMovementRepositoryAdapter(jpaRepository);

    @Test
    void shouldReturnSavedMovement_whenSavingStockMovement() {
        when(jpaRepository.save(any())).thenReturn(StockMovementModelMock.entrada(10));

        StockMovement domainMovement = new StockMovement(
                StockMovementModelMock.MOVEMENT_ID,
                StockMovementModelMock.MATERIAL_ID,
                null,
                "entrada",
                10,
                java.time.LocalDateTime.now()
        );

        StockMovement result = adapter.save(domainMovement);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(StockMovementModelMock.MOVEMENT_ID);
    }

    @Test
    void shouldDelegateToJpaRepository_whenSavingStockMovement() {
        when(jpaRepository.save(any())).thenReturn(StockMovementModelMock.entrada(5));

        StockMovement domainMovement = new StockMovement(
                StockMovementModelMock.MOVEMENT_ID,
                StockMovementModelMock.MATERIAL_ID,
                null,
                "entrada",
                5,
                java.time.LocalDateTime.now()
        );

        adapter.save(domainMovement);

        verify(jpaRepository).save(any());
    }

    @Test
    void shouldReturnMovements_whenFindByMaterialId() {
        when(jpaRepository.findByMaterialIdOrderByCreatedAtDesc(StockMovementModelMock.MATERIAL_ID))
                .thenReturn(List.of(StockMovementModelMock.entrada(10), StockMovementModelMock.reserva(3)));

        List<StockMovement> result = adapter.findByMaterialId(StockMovementModelMock.MATERIAL_ID);

        assertThat(result).hasSize(2);
    }

    @Test
    void shouldReturnEmptyList_whenFindByMaterialIdAndNoMovementsExist() {
        when(jpaRepository.findByMaterialIdOrderByCreatedAtDesc(StockMovementModelMock.MATERIAL_ID))
                .thenReturn(List.of());

        List<StockMovement> result = adapter.findByMaterialId(StockMovementModelMock.MATERIAL_ID);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldDelegateToJpaRepository_whenDeletingById() {
        adapter.deleteById(StockMovementModelMock.MOVEMENT_ID);

        verify(jpaRepository).deleteById(StockMovementModelMock.MOVEMENT_ID);
    }
}
