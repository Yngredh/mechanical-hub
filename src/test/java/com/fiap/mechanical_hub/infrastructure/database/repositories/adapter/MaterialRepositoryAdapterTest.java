package com.fiap.mechanical_hub.infrastructure.database.repositories.adapter;

import com.fiap.mechanical_hub.domain.entities.Material;
import com.fiap.mechanical_hub.infrastructure.database.models.MaterialModel;
import com.fiap.mechanical_hub.infrastructure.database.repositories.MaterialJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("MaterialRepositoryAdapter")
class MaterialRepositoryAdapterTest {

    @Mock
    private MaterialJpaRepository jpaRepository;

    @InjectMocks
    private MaterialRepositoryAdapter repositoryAdapter;

    private UUID materialId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Material material;
    private MaterialModel materialModel;

    @BeforeEach
    void setUp() {
        materialId = UUID.randomUUID();
        createdAt = LocalDateTime.of(2026, 5, 1, 9, 0);
        updatedAt = LocalDateTime.of(2026, 5, 1, 10, 0);

        material = new Material(
                materialId,
                "Filtro de Oleo",
                "Filtro para motor 1.6",
                new BigDecimal("45.90"),
                5,
                createdAt,
                updatedAt
        );

        materialModel = new MaterialModel(
                materialId,
                "Filtro de Oleo",
                "Filtro para motor 1.6",
                new BigDecimal("45.90"),
                5,
                createdAt,
                updatedAt
        );
    }

    @Test
    @DisplayName("save should persist and map to domain")
    void saveShouldPersistAndMapToDomain() {
        when(jpaRepository.save(any(MaterialModel.class))).thenReturn(materialModel);

        Material result = repositoryAdapter.save(material);

        assertThat(result).usingRecursiveComparison().isEqualTo(material);
        verify(jpaRepository).save(any(MaterialModel.class));
    }

    @Test
    @DisplayName("findById should return mapped domain when found")
    void findByIdShouldReturnMappedDomain() {
        when(jpaRepository.findById(materialId)).thenReturn(Optional.of(materialModel));

        Optional<Material> result = repositoryAdapter.findById(materialId);

        assertThat(result).isPresent();
        assertThat(result.get()).usingRecursiveComparison().isEqualTo(material);
        verify(jpaRepository).findById(materialId);
    }

    @Test
    @DisplayName("findAll should map all entities")
    void findAllShouldMapAllEntities() {
        when(jpaRepository.findAll()).thenReturn(List.of(materialModel));

        List<Material> result = repositoryAdapter.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).usingRecursiveComparison().isEqualTo(material);
        verify(jpaRepository).findAll();
    }

    @Test
    @DisplayName("deleteById should delegate to JPA repository")
    void deleteByIdShouldDelegateToJpaRepository() {
        repositoryAdapter.deleteById(materialId);

        verify(jpaRepository).deleteById(materialId);
    }
}

