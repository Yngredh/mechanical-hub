package com.fiap.mechanical_hub.infrastructure.http.controllers;

import com.fiap.mechanical_hub.application.dto.service.ServiceResponse;
import com.fiap.mechanical_hub.application.dto.service.UpsertServiceRequest;
import com.fiap.mechanical_hub.application.usecases.service.CreateServiceUseCase;
import com.fiap.mechanical_hub.application.usecases.service.DeleteServiceUseCase;
import com.fiap.mechanical_hub.application.usecases.service.FindAllServicesUseCase;
import com.fiap.mechanical_hub.application.usecases.service.FindServiceByIdUseCase;
import com.fiap.mechanical_hub.application.usecases.service.UpdateServiceUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ServiceControllerTest {

    private static final UUID SERVICE_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    private final CreateServiceUseCase createServiceUseCase = mock(CreateServiceUseCase.class);
    private final UpdateServiceUseCase updateServiceUseCase = mock(UpdateServiceUseCase.class);
    private final FindServiceByIdUseCase findServiceByIdUseCase = mock(FindServiceByIdUseCase.class);
    private final FindAllServicesUseCase findAllServicesUseCase = mock(FindAllServicesUseCase.class);
    private final DeleteServiceUseCase deleteServiceUseCase = mock(DeleteServiceUseCase.class);

    private final ServiceController controller = new ServiceController(
            createServiceUseCase, updateServiceUseCase, findServiceByIdUseCase,
            findAllServicesUseCase, deleteServiceUseCase
    );

    private UpsertServiceRequest buildRequest() {
        return new UpsertServiceRequest("Troca de óleo", "Troca de óleo e filtro",
                BigDecimal.valueOf(50.00), BigDecimal.valueOf(80.00), List.of());
    }

    @Test
    void shouldReturnCreated_whenServiceIsCreated() {
        when(createServiceUseCase.execute(any())).thenReturn(mock(ServiceResponse.class));

        ResponseEntity<ServiceResponse> response = controller.create(buildRequest());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void shouldDelegateToCreateUseCase_whenCreatingService() {
        when(createServiceUseCase.execute(any())).thenReturn(mock(ServiceResponse.class));

        controller.create(buildRequest());

        verify(createServiceUseCase).execute(any());
    }

    @Test
    void shouldReturnOk_whenFindingAllServices() {
        when(findAllServicesUseCase.execute()).thenReturn(List.of(mock(ServiceResponse.class)));

        ResponseEntity<List<ServiceResponse>> response = controller.findAll();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void shouldReturnOk_whenFindingServiceById() {
        when(findServiceByIdUseCase.execute(any())).thenReturn(mock(ServiceResponse.class));

        ResponseEntity<ServiceResponse> response = controller.findById(SERVICE_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldReturnOk_whenUpdatingService() {
        when(updateServiceUseCase.execute(any())).thenReturn(mock(ServiceResponse.class));

        ResponseEntity<ServiceResponse> response = controller.update(SERVICE_ID, buildRequest());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldReturnNoContent_whenDeletingService() {
        ResponseEntity<Void> response = controller.delete(SERVICE_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(deleteServiceUseCase).execute(any());
    }
}
