package com.fiap.mechanical_hub.infrastructure.database.adapter;

import com.fiap.mechanical_hub.domain.entities.Stock;
import com.fiap.mechanical_hub.domain.enums.StockStatusEnum;
import com.fiap.mechanical_hub.mocks.domain.entities.StockMock;
import com.fiap.mechanical_hub.mocks.infrastructure.database.models.StockModelMock;
import com.fiap.mechanical_hub.infrastructure.database.repositories.StockJpaRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StockRepositoryAdapterTest {

    private final StockJpaRepository jpaRepository = mock(StockJpaRepository.class);

    private final StockRepositoryAdapter adapter = new StockRepositoryAdapter(jpaRepository);

    @Test
    void shouldReturnSavedStock_whenSavingStock() {
        when(jpaRepository.save(any())).thenReturn(StockModelMock.available(10));

        Stock result = adapter.save(StockMock.available(10));

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(StockModelMock.STOCK_ID);
    }

    @Test
    void shouldDelegateToJpaRepository_whenSavingStock() {
        when(jpaRepository.save(any())).thenReturn(StockModelMock.available(10));

        adapter.save(StockMock.available(10));

        verify(jpaRepository).save(any());
    }

    @Test
    void shouldReturnStock_whenFindByIdAndStockExists() {
        when(jpaRepository.findById(StockModelMock.STOCK_ID))
                .thenReturn(Optional.of(StockModelMock.available(10)));

        Optional<Stock> result = adapter.findById(StockModelMock.STOCK_ID);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(StockModelMock.STOCK_ID);
    }

    @Test
    void shouldReturnEmpty_whenFindByIdAndStockDoesNotExist() {
        when(jpaRepository.findById(StockModelMock.STOCK_ID)).thenReturn(Optional.empty());

        Optional<Stock> result = adapter.findById(StockModelMock.STOCK_ID);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnStock_whenFindByMaterialIdAndStatusAndExists() {
        when(jpaRepository.findByMaterialIdAndStatus(StockModelMock.MATERIAL_ID, StockStatusEnum.AVAILABLE))
                .thenReturn(Optional.of(StockModelMock.available(10)));

        Optional<Stock> result = adapter.findByMaterialIdAndStatus(StockModelMock.MATERIAL_ID, StockStatusEnum.AVAILABLE);

        assertThat(result).isPresent();
        assertThat(result.get().getStatus()).isEqualTo(StockStatusEnum.AVAILABLE);
    }

    @Test
    void shouldReturnAllStocks_whenFindAll() {
        when(jpaRepository.findAll()).thenReturn(List.of(StockModelMock.available(10)));

        List<Stock> result = adapter.findAll();

        assertThat(result).hasSize(1);
    }

    @Test
    void shouldReturnStocksByMaterialId_whenFindAllByMaterialId() {
        when(jpaRepository.findAllByMaterialId(StockModelMock.MATERIAL_ID))
                .thenReturn(List.of(StockModelMock.available(5), StockModelMock.reserved(3)));

        List<Stock> result = adapter.findAllByMaterialId(StockModelMock.MATERIAL_ID);

        assertThat(result).hasSize(2);
    }

    @Test
    void shouldDelegateToJpaRepository_whenDeletingById() {
        adapter.deleteById(StockModelMock.STOCK_ID);

        verify(jpaRepository).deleteById(StockModelMock.STOCK_ID);
    }

    @Test
    void shouldDeleteStock_whenDeleteByMaterialIdAndStockExists() {
        when(jpaRepository.findByMaterialId(StockModelMock.MATERIAL_ID))
                .thenReturn(Optional.of(StockModelMock.available(10)));

        adapter.deleteByMaterialId(StockModelMock.MATERIAL_ID);

        verify(jpaRepository).deleteById(StockModelMock.STOCK_ID);
    }

    @Test
    void shouldNotDeleteAnything_whenDeleteByMaterialIdAndStockDoesNotExist() {
        when(jpaRepository.findByMaterialId(StockModelMock.MATERIAL_ID)).thenReturn(Optional.empty());

        adapter.deleteByMaterialId(StockModelMock.MATERIAL_ID);

        verify(jpaRepository).findByMaterialId(StockModelMock.MATERIAL_ID);
    }
}
