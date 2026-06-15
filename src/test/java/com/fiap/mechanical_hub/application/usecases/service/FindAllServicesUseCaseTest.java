package com.fiap.mechanical_hub.application.usecases.service;

import com.fiap.mechanical_hub.application.dto.service.ServiceResponse;
import com.fiap.mechanical_hub.domain.entities.ServiceData;
import com.fiap.mechanical_hub.domain.repositories.ServiceRepository;
import com.fiap.mechanical_hub.mocks.domain.entities.ServiceDataMock;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FindAllServicesUseCaseTest {

    private final ServiceRepository serviceRepository = mock(ServiceRepository.class);
    private final FindAllServicesUseCase useCase = new FindAllServicesUseCase(serviceRepository);

    @Test
    void shouldReturnAllServices_whenServicesExist() {
        ServiceData serviceData = ServiceDataMock.withDefaultValues();
        when(serviceRepository.findAll()).thenReturn(List.of(serviceData));

        List<ServiceResponse> result = useCase.execute();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getName()).isEqualTo(serviceData.getName());
    }

    @Test
    void shouldReturnEmptyList_whenNoServicesExist() {
        when(serviceRepository.findAll()).thenReturn(List.of());

        List<ServiceResponse> result = useCase.execute();

        assertThat(result).isEmpty();
    }

    @Test
    void shouldQueryRepository_whenExecuted() {
        when(serviceRepository.findAll()).thenReturn(List.of());

        useCase.execute();

        verify(serviceRepository).findAll();
    }
}
