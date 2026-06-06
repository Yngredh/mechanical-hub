package com.fiap.mechanical_hub.application.usecases.service;

import com.fiap.mechanical_hub.domain.entities.ServiceData;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.domain.repositories.ServiceRepository;
import com.fiap.mechanical_hub.mocks.domain.entities.ServiceDataMock;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GetServiceByIdUseCaseTest {

    private static final UUID SERVICE_ID = UUID.fromString("00000000-0000-0000-0000-000000000040");

    private final ServiceRepository serviceRepository = mock(ServiceRepository.class);
    private final GetServiceByIdUseCase useCase = new GetServiceByIdUseCase(serviceRepository);

    @Test
    void shouldReturnServiceData_whenServiceExists() {
        ServiceData serviceData = ServiceDataMock.withDefaultValues();

        when(serviceRepository.findById(SERVICE_ID)).thenReturn(Optional.of(serviceData));

        ServiceData result = useCase.execute(SERVICE_ID);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(serviceData.getId());
        assertThat(result.getName()).isEqualTo(serviceData.getName());
    }

    @Test
    void shouldThrowNotFoundException_whenServiceDoesNotExist() {
        UUID unknownServiceId = UUID.fromString("00000000-0000-0000-0000-000000000099");

        when(serviceRepository.findById(unknownServiceId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(unknownServiceId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Serviço não encontrado");
    }

    @Test
    void shouldQueryRepositoryById_whenExecuted() {
        ServiceData serviceData = ServiceDataMock.withDefaultValues();

        when(serviceRepository.findById(SERVICE_ID)).thenReturn(Optional.of(serviceData));

        useCase.execute(SERVICE_ID);

        verify(serviceRepository).findById(SERVICE_ID);
    }
}
