package com.fiap.mechanical_hub.infrastructure.http.controllers;

import com.fiap.mechanical_hub.application.dto.vehicle.InsertVehicleRequest;
import com.fiap.mechanical_hub.application.dto.vehicle.UpdateVehicleRequest;
import com.fiap.mechanical_hub.application.dto.vehicle.VehicleResponse;
import com.fiap.mechanical_hub.application.usecases.vehicle.CreateVehicleUseCase;
import com.fiap.mechanical_hub.application.usecases.vehicle.DeleteVehicleUseCase;
import com.fiap.mechanical_hub.application.usecases.vehicle.FindAllVehiclesUseCase;
import com.fiap.mechanical_hub.application.usecases.vehicle.FindVehicleByIdUseCase;
import com.fiap.mechanical_hub.application.usecases.vehicle.UpdateVehicleUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class VehicleControllerTest {

    private static final UUID VEHICLE_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID CUSTOMER_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");

    private final CreateVehicleUseCase createVehicleUseCase = mock(CreateVehicleUseCase.class);
    private final FindVehicleByIdUseCase findVehicleByIdUseCase = mock(FindVehicleByIdUseCase.class);
    private final FindAllVehiclesUseCase findAllVehiclesUseCase = mock(FindAllVehiclesUseCase.class);
    private final UpdateVehicleUseCase updateVehicleUseCase = mock(UpdateVehicleUseCase.class);
    private final DeleteVehicleUseCase deleteVehicleUseCase = mock(DeleteVehicleUseCase.class);

    private final VehicleController controller = new VehicleController(
            createVehicleUseCase, findVehicleByIdUseCase, findAllVehiclesUseCase,
            updateVehicleUseCase, deleteVehicleUseCase
    );

    @Test
    void shouldReturnCreated_whenVehicleIsCreated() {
        InsertVehicleRequest request = new InsertVehicleRequest("ABC-1234", "Toyota", "Corolla", 2020, "Preto");
        when(createVehicleUseCase.execute(any())).thenReturn(mock(VehicleResponse.class));

        ResponseEntity<VehicleResponse> response = controller.create(CUSTOMER_ID, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void shouldDelegateToCreateUseCase_whenCreatingVehicle() {
        InsertVehicleRequest request = new InsertVehicleRequest("ABC-1234", "Toyota", "Corolla", 2020, "Preto");
        when(createVehicleUseCase.execute(any())).thenReturn(mock(VehicleResponse.class));

        controller.create(CUSTOMER_ID, request);

        verify(createVehicleUseCase).execute(any());
    }

    @Test
    void shouldReturnOk_whenFindingAllVehicles() {
        when(findAllVehiclesUseCase.execute()).thenReturn(List.of(mock(VehicleResponse.class)));

        ResponseEntity<List<VehicleResponse>> response = controller.findAll();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void shouldReturnOk_whenFindingVehicleById() {
        when(findVehicleByIdUseCase.execute(VEHICLE_ID)).thenReturn(mock(VehicleResponse.class));

        ResponseEntity<VehicleResponse> response = controller.findById(VEHICLE_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldReturnOk_whenUpdatingVehicle() {
        UpdateVehicleRequest request = new UpdateVehicleRequest("Toyota", "Corolla", 2021, "Branco");
        when(updateVehicleUseCase.execute(any())).thenReturn(mock(VehicleResponse.class));

        ResponseEntity<VehicleResponse> response = controller.update(VEHICLE_ID, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
