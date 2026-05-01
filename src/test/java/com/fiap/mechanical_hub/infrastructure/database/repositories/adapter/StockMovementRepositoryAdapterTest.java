package com.fiap.mechanical_hub.infrastructure.database.repositories.adapter;

import com.fiap.mechanical_hub.domain.entities.StockMovement;
import com.fiap.mechanical_hub.infrastructure.database.models.StockMovementModel;
import com.fiap.mechanical_hub.infrastructure.database.repositories.StockMovementJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StockMovementRepositoryAdapter")
class StockMovementRepositoryAdapterTest {

    @Mock
    private StockMovementJpaRepository jpaRepository;

    @InjectMocks
    private StockMovementRepositoryAdapter repositoryAdapter;

    private UUID stockMovementId;
    private UUID materialId;

    private StockMovement stockMovement;
    private StockMovementModel stockMovementModel;

    @BeforeEach
    void setUp() {
        stockMovementId = UUID.randomUUID();
        materialId = UUID.randomUUID();

        stockMovement = mock(StockMovement.class);
        stockMovementModel = mock(StockMovementModel.class);
    }

    @Test
    @DisplayName("save should map, persist and return domain entity")
    void saveShouldMapPersistAndReturnDomain() {
        try (MockedStatic<com.fiap.mechanical_hub.application.mappers.StockMovementMapper> mapperMock =
                     mockStatic(com.fiap.mechanical_hub.application.mappers.StockMovementMapper.class)) {

            mapperMock.when(() ->
                    com.fiap.mechanical_hub.application.mappers.StockMovementMapper.toJpaEntity(stockMovement)
            ).thenReturn(stockMovementModel);

            when(jpaRepository.save(stockMovementModel)).thenReturn(stockMovementModel);

            mapperMock.when(() ->
                    com.fiap.mechanical_hub.application.mappers.StockMovementMapper.toDomainEntity(stockMovementModel)
            ).thenReturn(stockMovement);

            StockMovement result = repositoryAdapter.save(stockMovement);

            assertThat(result).isEqualTo(stockMovement);

            mapperMock.verify(() ->
                    com.fiap.mechanical_hub.application.mappers.StockMovementMapper.toJpaEntity(stockMovement)
            );
            mapperMock.verify(() ->
                    com.fiap.mechanical_hub.application.mappers.StockMovementMapper.toDomainEntity(stockMovementModel)
            );
            verify(jpaRepository).save(stockMovementModel);
        }
    }

    @Test
    @DisplayName("deleteById should delegate to JPA repository")
    void deleteByIdShouldDelegateToJpaRepository() {
        repositoryAdapter.deleteById(stockMovementId);

        verify(jpaRepository).deleteById(stockMovementId);
    }

    @Test
    @DisplayName("findByMaterialId should map and return ordered list")
    void findByMaterialIdShouldMapAndReturnList() {
        try (MockedStatic<com.fiap.mechanical_hub.application.mappers.StockMovementMapper> mapperMock =
                     mockStatic(com.fiap.mechanical_hub.application.mappers.StockMovementMapper.class)) {

            when(jpaRepository.findByMaterialIdOrderByCreatedAtDesc(materialId))
                    .thenReturn(List.of(stockMovementModel));

            mapperMock.when(() ->
                    com.fiap.mechanical_hub.application.mappers.StockMovementMapper.toDomainEntity(stockMovementModel)
            ).thenReturn(stockMovement);

            List<StockMovement> result = repositoryAdapter.findByMaterialId(materialId);

            assertThat(result).hasSize(1);
            assertThat(result.get(0)).isEqualTo(stockMovement);

            verify(jpaRepository).findByMaterialIdOrderByCreatedAtDesc(materialId);
            mapperMock.verify(() ->
                    com.fiap.mechanical_hub.application.mappers.StockMovementMapper.toDomainEntity(stockMovementModel)
            );
        }
    }
}
