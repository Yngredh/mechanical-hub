package com.fiap.mechanical_hub.infrastructure.database.adapter;

import com.fiap.mechanical_hub.domain.entities.ServiceData;
import com.fiap.mechanical_hub.mocks.domain.entities.ServiceDataMock;
import com.fiap.mechanical_hub.mocks.infrastructure.database.models.ServiceModelMock;
import com.fiap.mechanical_hub.infrastructure.database.repositories.ServiceJpaRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ServiceRepositoryAdapterTest {

    private final ServiceJpaRepository jpaRepository = mock(ServiceJpaRepository.class);

    private final ServiceRepositoryAdapter adapter = new ServiceRepositoryAdapter(jpaRepository);

    @Test
    void shouldReturnSavedService_whenSavingService() {
        when(jpaRepository.save(any())).thenReturn(ServiceModelMock.withOneMaterial());

        ServiceData result = adapter.save(ServiceDataMock.withDefaultValues());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(ServiceModelMock.SERVICE_ID);
    }

    @Test
    void shouldDelegateToJpaRepository_whenSavingService() {
        when(jpaRepository.save(any())).thenReturn(ServiceModelMock.withOneMaterial());

        adapter.save(ServiceDataMock.withDefaultValues());

        verify(jpaRepository).save(any());
    }

    @Test
    void shouldReturnService_whenFindByIdAndServiceExists() {
        when(jpaRepository.findByIdAndDeletedAtIsNull(ServiceModelMock.SERVICE_ID))
                .thenReturn(Optional.of(ServiceModelMock.withOneMaterial()));

        Optional<ServiceData> result = adapter.findById(ServiceModelMock.SERVICE_ID);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(ServiceModelMock.SERVICE_ID);
    }

    @Test
    void shouldReturnEmpty_whenFindByIdAndServiceDoesNotExist() {
        when(jpaRepository.findByIdAndDeletedAtIsNull(ServiceModelMock.SERVICE_ID))
                .thenReturn(Optional.empty());

        Optional<ServiceData> result = adapter.findById(ServiceModelMock.SERVICE_ID);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnAllServices_whenFindAll() {
        when(jpaRepository.findByDeletedAtIsNull())
                .thenReturn(List.of(ServiceModelMock.withOneMaterial()));

        List<ServiceData> result = adapter.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(ServiceModelMock.SERVICE_ID);
    }

    @Test
    void shouldReturnServices_whenFindAllIn() {
        List<UUID> ids = List.of(ServiceModelMock.SERVICE_ID);
        when(jpaRepository.findAllIn(ids)).thenReturn(List.of(ServiceModelMock.withOneMaterial()));

        List<ServiceData> result = adapter.findAllIn(ids);

        assertThat(result).hasSize(1);
    }

    @Test
    void shouldDelegateToJpaRepository_whenDeletingById() {
        adapter.deleteById(ServiceModelMock.SERVICE_ID);

        verify(jpaRepository).softDelete(ServiceModelMock.SERVICE_ID);
    }
}
