package com.fiap.mechanical_hub.infrastructure.database.repositories.adapter;

import com.fiap.mechanical_hub.domain.entities.Stock;
import com.fiap.mechanical_hub.domain.enums.StockStatusEnum;
import com.fiap.mechanical_hub.infrastructure.database.models.StockModel;
import com.fiap.mechanical_hub.infrastructure.database.repositories.StockJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("StockRepositoryAdapter")
class StockRepositoryAdapterTest {

    @Mock
    private StockJpaRepository jpaRepository;

    @InjectMocks
    private StockRepositoryAdapter repositoryAdapter;

    private UUID stockId;
    private UUID materialId;
    private Stock stock;
    private StockModel stockModel;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        stockId = UUID.randomUUID();
        materialId = UUID.randomUUID();
        now = LocalDateTime.now();

        stock = new Stock(
                stockId,
                materialId,
                10,
                StockStatusEnum.AVAILABLE,
                now
        );

        stockModel = new StockModel(
                stockId,
                materialId,
                10,
                StockStatusEnum.AVAILABLE,
                now
        );
    }

    @Test
    @DisplayName("save should persist and return mapped domain entity")
    void saveShouldPersistAndReturnMappedDomain() {
        when(jpaRepository.save(org.mockito.Mockito.any(StockModel.class)))
                .thenReturn(stockModel);

        Stock result = repositoryAdapter.save(stock);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(stockId);
        assertThat(result.getMaterialId()).isEqualTo(materialId);

        verify(jpaRepository).save(org.mockito.Mockito.any(StockModel.class));
    }

    @Test
    @DisplayName("findByMaterialId should return mapped domain when found")
    void findByMaterialIdShouldReturnMappedDomain() {
        when(jpaRepository.findByMaterialId(materialId))
                .thenReturn(Optional.of(stockModel));

        Optional<Stock> result = repositoryAdapter.findByMaterialId(materialId);

        assertThat(result).isPresent();
        verify(jpaRepository).findByMaterialId(materialId);
    }

    @Test
    @DisplayName("findByMaterialId should return empty when not found")
    void findByMaterialIdShouldReturnEmpty() {
        when(jpaRepository.findByMaterialId(materialId))
                .thenReturn(Optional.empty());

        Optional<Stock> result = repositoryAdapter.findByMaterialId(materialId);

        assertThat(result).isEmpty();
        verify(jpaRepository).findByMaterialId(materialId);
    }

    @Test
    @DisplayName("findByMaterialIdAndStatus should return mapped domain when found")
    void findByMaterialIdAndStatusShouldReturnMappedDomain() {
        when(jpaRepository.findByMaterialIdAndStatus(materialId, StockStatusEnum.AVAILABLE))
                .thenReturn(Optional.of(stockModel));

        Optional<Stock> result =
                repositoryAdapter.findByMaterialIdAndStatus(materialId, StockStatusEnum.AVAILABLE);

        assertThat(result).isPresent();
        verify(jpaRepository).findByMaterialIdAndStatus(materialId, StockStatusEnum.AVAILABLE);
    }

    @Test
    @DisplayName("findByMaterialIdAndStatus should return empty when not found")
    void findByMaterialIdAndStatusShouldReturnEmpty() {
        when(jpaRepository.findByMaterialIdAndStatus(materialId, StockStatusEnum.AVAILABLE))
                .thenReturn(Optional.empty());

        Optional<Stock> result =
                repositoryAdapter.findByMaterialIdAndStatus(materialId, StockStatusEnum.AVAILABLE);

        assertThat(result).isEmpty();
        verify(jpaRepository).findByMaterialIdAndStatus(materialId, StockStatusEnum.AVAILABLE);
    }

    @Test
    @DisplayName("findAll should map all entities")
    void findAllShouldMapAllEntities() {
        when(jpaRepository.findAll()).thenReturn(List.of(stockModel));

        List<Stock> result = repositoryAdapter.findAll();

        assertThat(result).hasSize(1);
        verify(jpaRepository).findAll();
    }

    @Test
    @DisplayName("findAllByMaterialId should map filtered entities")
    void findAllByMaterialIdShouldMapEntities() {
        when(jpaRepository.findAllByMaterialId(materialId))
                .thenReturn(List.of(stockModel));

        List<Stock> result = repositoryAdapter.findAllByMaterialId(materialId);

        assertThat(result).hasSize(1);
        verify(jpaRepository).findAllByMaterialId(materialId);
    }

    @Test
    @DisplayName("deleteById should delegate to JPA repository")
    void deleteByIdShouldDelegateToJpaRepository() {
        repositoryAdapter.deleteById(stockId);

        verify(jpaRepository).deleteById(stockId);
    }
}
