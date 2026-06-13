package com.fiap.mechanical_hub.infrastructure.database.adapter;

import com.fiap.mechanical_hub.domain.entities.StockPendingItem;
import com.fiap.mechanical_hub.mocks.infrastructure.database.models.StockPendingItemModelMock;
import com.fiap.mechanical_hub.infrastructure.database.repositories.StockPendingItemJpaRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StockPendingItemRepositoryAdapterTest {

    private final StockPendingItemJpaRepository jpaRepository = mock(StockPendingItemJpaRepository.class);

    private final StockPendingItemRepositoryAdapter adapter = new StockPendingItemRepositoryAdapter(jpaRepository);

    private StockPendingItem buildPendingItem() {
        return new StockPendingItem(
                StockPendingItemModelMock.PENDING_ITEM_ID,
                StockPendingItemModelMock.SERVICE_ORDER_ID,
                StockPendingItemModelMock.MATERIAL_ID,
                3,
                LocalDateTime.of(2024, 1, 1, 10, 0)
        );
    }

    @Test
    void shouldReturnSavedPendingItem_whenSavingStockPendingItem() {
        when(jpaRepository.save(any())).thenReturn(StockPendingItemModelMock.withDefaultValues());

        StockPendingItem result = adapter.save(buildPendingItem());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(StockPendingItemModelMock.PENDING_ITEM_ID);
    }

    @Test
    void shouldDelegateToJpaRepository_whenSavingStockPendingItem() {
        when(jpaRepository.save(any())).thenReturn(StockPendingItemModelMock.withDefaultValues());

        adapter.save(buildPendingItem());

        verify(jpaRepository).save(any());
    }

    @Test
    void shouldReturnPendingItem_whenFindByIdAndExists() {
        when(jpaRepository.findById(StockPendingItemModelMock.PENDING_ITEM_ID))
                .thenReturn(Optional.of(StockPendingItemModelMock.withDefaultValues()));

        Optional<StockPendingItem> result = adapter.findById(StockPendingItemModelMock.PENDING_ITEM_ID);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(StockPendingItemModelMock.PENDING_ITEM_ID);
    }

    @Test
    void shouldReturnEmpty_whenFindByIdAndNotExists() {
        when(jpaRepository.findById(StockPendingItemModelMock.PENDING_ITEM_ID)).thenReturn(Optional.empty());

        Optional<StockPendingItem> result = adapter.findById(StockPendingItemModelMock.PENDING_ITEM_ID);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnPendingItems_whenFindByMaterialIdOrderByCreatedAtAsc() {
        when(jpaRepository.findByMaterialIdOrderByCreatedAtAsc(StockPendingItemModelMock.MATERIAL_ID))
                .thenReturn(List.of(StockPendingItemModelMock.withDefaultValues()));

        List<StockPendingItem> result = adapter.findByMaterialIdOrderByCreatedAtAsc(StockPendingItemModelMock.MATERIAL_ID);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMaterialId()).isEqualTo(StockPendingItemModelMock.MATERIAL_ID);
    }

    @Test
    void shouldDelegateToJpaRepository_whenDeletingById() {
        adapter.deleteById(StockPendingItemModelMock.PENDING_ITEM_ID);

        verify(jpaRepository).deleteById(StockPendingItemModelMock.PENDING_ITEM_ID);
    }

    @Test
    void shouldDelegateToJpaRepository_whenDeletingPendingItem() {
        adapter.delete(buildPendingItem());

        verify(jpaRepository).delete(any());
    }
}
