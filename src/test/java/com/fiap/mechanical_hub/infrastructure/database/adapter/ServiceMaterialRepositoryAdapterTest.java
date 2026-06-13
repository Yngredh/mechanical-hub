package com.fiap.mechanical_hub.infrastructure.database.adapter;

import com.fiap.mechanical_hub.domain.entities.ServiceMaterial;
import com.fiap.mechanical_hub.mocks.domain.entities.ServiceDataMock;
import com.fiap.mechanical_hub.mocks.domain.entities.ServiceMaterialMock;
import com.fiap.mechanical_hub.mocks.infrastructure.database.models.MaterialModelMock;
import com.fiap.mechanical_hub.mocks.infrastructure.database.models.ServiceMaterialModelMock;
import com.fiap.mechanical_hub.mocks.infrastructure.database.models.ServiceModelMock;
import com.fiap.mechanical_hub.infrastructure.database.repositories.ServiceMaterialJpaRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ServiceMaterialRepositoryAdapterTest {

    private final ServiceMaterialJpaRepository jpaRepository = mock(ServiceMaterialJpaRepository.class);

    private final ServiceMaterialRepositoryAdapter adapter = new ServiceMaterialRepositoryAdapter(jpaRepository);

    @Test
    void shouldReturnSavedServiceMaterial_whenSavingServiceMaterial() {
        when(jpaRepository.save(any())).thenReturn(ServiceMaterialModelMock.withDefaultValues());

        ServiceMaterial result = adapter.save(
                ServiceMaterialMock.withDefaultValues(),
                ServiceDataMock.withDefaultValues()
        );

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(ServiceMaterialModelMock.SERVICE_MATERIAL_ID);
    }

    @Test
    void shouldDelegateToJpaRepository_whenSavingServiceMaterial() {
        when(jpaRepository.save(any())).thenReturn(ServiceMaterialModelMock.withDefaultValues());

        adapter.save(ServiceMaterialMock.withDefaultValues(), ServiceDataMock.withDefaultValues());

        verify(jpaRepository).save(any());
    }

    @Test
    void shouldReturnServiceMaterials_whenFindByServiceId() {
        when(jpaRepository.findByServiceId(ServiceModelMock.SERVICE_ID))
                .thenReturn(List.of(ServiceMaterialModelMock.withDefaultValues()));

        List<ServiceMaterial> result = adapter.findByServiceId(ServiceModelMock.SERVICE_ID);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(ServiceMaterialModelMock.SERVICE_MATERIAL_ID);
    }

    @Test
    void shouldReturnEmptyList_whenFindByServiceIdAndNoneExist() {
        when(jpaRepository.findByServiceId(ServiceModelMock.SERVICE_ID)).thenReturn(List.of());

        List<ServiceMaterial> result = adapter.findByServiceId(ServiceModelMock.SERVICE_ID);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnServiceMaterials_whenFindByMaterialId() {
        when(jpaRepository.findByMaterialId(MaterialModelMock.MATERIAL_ID))
                .thenReturn(List.of(ServiceMaterialModelMock.withDefaultValues()));

        List<ServiceMaterial> result = adapter.findByMaterialId(MaterialModelMock.MATERIAL_ID);

        assertThat(result).hasSize(1);
    }
}
