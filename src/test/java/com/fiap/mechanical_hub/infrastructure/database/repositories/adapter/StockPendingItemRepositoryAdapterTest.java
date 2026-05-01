package com.fiap.mechanical_hub.infrastructure.database.repositories.adapter;

import com.fiap.mechanical_hub.domain.entities.StockPendingItem;
import com.fiap.mechanical_hub.infrastructure.database.models.StockPendingItemModel;
import com.fiap.mechanical_hub.infrastructure.database.repositories.StockPendingItemJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StockPendingItemRepositoryAdapter")
class StockPendingItemRepositoryAdapterTest {

    @Mock
    private StockPendingItemJpaRepository jpaRepository;

    @InjectMocks
    private StockPendingItemRepositoryAdapter repositoryAdapter;

    private UUID id;
    private UUID serviceOrderId;
    private UUID materialId;
    private LocalDateTime createdAt;

    private StockPendingItem domain;
    private StockPendingItemModel model;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        serviceOrderId = UUID.randomUUID();
        materialId = UUID.randomUUID();
        createdAt = LocalDateTime.now();

        domain = new StockPendingItem(
                id,
                serviceOrderId,
                materialId,
                10,
                createdAt
        );

        model = new StockPendingItemModel(
                id,
                serviceOrderId,
                materialId,
                10,
                createdAt
        );
    }

    @Test
    @DisplayName("save should persist and return mapped domain entity")
    void saveShouldPersistAndReturnMappedDomain() {
        when(jpaRepository.save(any(StockPendingItemModel.class)))
                .thenReturn(model);

        StockPendingItem result = repositoryAdapter.save(domain);

        ArgumentCaptor<StockPendingItemModel> captor =
                ArgumentCaptor.forClass(StockPendingItemModel.class);

        verify(jpaRepository).save(captor.capture());

        StockPendingItemModel captured = captor.getValue();

        assertThat(captured.getId()).isEqualTo(domain.getId());
        assertThat(captured.getMaterialId()).isEqualTo(domain.getMaterialId());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(model.getId());
    }

    @Test
    @DisplayName("findById should return mapped domain when found")
    void findByIdShouldReturnMappedDomain() {
        when(jpaRepository.findById(id)).thenReturn(Optional.of(model));

        Optional<StockPendingItem> result = repositoryAdapter.findById(id);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(id);
        verify(jpaRepository).findById(id);
    }

    @Test
    @DisplayName("findById should return empty when not found")
    void findByIdShouldReturnEmptyWhenNotFound() {
        when(jpaRepository.findById(id)).thenReturn(Optional.empty());

        Optional<StockPendingItem> result = repositoryAdapter.findById(id);

        assertThat(result).isEmpty();
        verify(jpaRepository).findById(id);
    }

    @Test
    @DisplayName("findByMaterialIdOrderByCreatedAtAsc should map list")
    void findByMaterialIdOrderByCreatedAtAscShouldMapList() {
        when(jpaRepository.findByMaterialIdOrderByCreatedAtAsc(materialId))
                .thenReturn(List.of(model));

        List<StockPendingItem> result =
                repositoryAdapter.findByMaterialIdOrderByCreatedAtAsc(materialId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMaterialId()).isEqualTo(materialId);
        verify(jpaRepository).findByMaterialIdOrderByCreatedAtAsc(materialId);
    }

    @Test
    @DisplayName("findByServiceOrderId should map list")
    void findByServiceOrderIdShouldMapList() {
        when(jpaRepository.findByServiceOrderId(serviceOrderId))
                .thenReturn(List.of(model));

        List<StockPendingItem> result =
                repositoryAdapter.findByServiceOrderId(serviceOrderId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getServiceOrderId()).isEqualTo(serviceOrderId);
        verify(jpaRepository).findByServiceOrderId(serviceOrderId);
    }

    @Test
    @DisplayName("deleteById should delegate to JPA repository")
    void deleteByIdShouldDelegateToJpaRepository() {
        repositoryAdapter.deleteById(id);

        verify(jpaRepository).deleteById(id);
    }

    @Test
    @DisplayName("delete should convert and delegate to JPA repository")
    void deleteShouldConvertAndDelegate() {
        repositoryAdapter.delete(domain);

        ArgumentCaptor<StockPendingItemModel> captor =
                ArgumentCaptor.forClass(StockPendingItemModel.class);

        verify(jpaRepository).delete(captor.capture());

        StockPendingItemModel captured = captor.getValue();

        assertThat(captured.getId()).isEqualTo(domain.getId());
        assertThat(captured.getMaterialId()).isEqualTo(domain.getMaterialId());
    }
}