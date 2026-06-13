package com.fiap.mechanical_hub.infrastructure.database.adapter;

import com.fiap.mechanical_hub.domain.entities.Material;
import com.fiap.mechanical_hub.mocks.domain.entities.MaterialMock;
import com.fiap.mechanical_hub.mocks.infrastructure.database.models.MaterialModelMock;
import com.fiap.mechanical_hub.infrastructure.database.repositories.MaterialJpaRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MaterialRepositoryAdapterTest {

    private final MaterialJpaRepository jpaRepository = mock(MaterialJpaRepository.class);

    private final MaterialRepositoryAdapter adapter = new MaterialRepositoryAdapter(jpaRepository);

    @Test
    void shouldReturnSavedMaterial_whenSavingMaterial() {
        when(jpaRepository.save(any())).thenReturn(MaterialModelMock.withDefaultValues());

        Material result = adapter.save(MaterialMock.withSufficientStock());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(MaterialModelMock.MATERIAL_ID);
    }

    @Test
    void shouldDelegateToJpaRepository_whenSavingMaterial() {
        when(jpaRepository.save(any())).thenReturn(MaterialModelMock.withDefaultValues());

        adapter.save(MaterialMock.withSufficientStock());

        verify(jpaRepository).save(any());
    }

    @Test
    void shouldReturnMaterial_whenFindByIdAndMaterialExists() {
        when(jpaRepository.findByIdAndDeletedAtIsNull(MaterialModelMock.MATERIAL_ID))
                .thenReturn(Optional.of(MaterialModelMock.withDefaultValues()));

        Optional<Material> result = adapter.findById(MaterialModelMock.MATERIAL_ID);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(MaterialModelMock.MATERIAL_ID);
    }

    @Test
    void shouldReturnEmpty_whenFindByIdAndMaterialDoesNotExist() {
        when(jpaRepository.findByIdAndDeletedAtIsNull(MaterialModelMock.MATERIAL_ID))
                .thenReturn(Optional.empty());

        Optional<Material> result = adapter.findById(MaterialModelMock.MATERIAL_ID);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnAllMaterials_whenFindAll() {
        when(jpaRepository.findByDeletedAtIsNull())
                .thenReturn(List.of(MaterialModelMock.withDefaultValues()));

        List<Material> result = adapter.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(MaterialModelMock.MATERIAL_ID);
    }

    @Test
    void shouldDelegateToJpaRepository_whenDeletingById() {
        adapter.deleteById(MaterialModelMock.MATERIAL_ID);

        verify(jpaRepository).deleteById(MaterialModelMock.MATERIAL_ID);
    }
}
