package com.fiap.mechanical_hub.infrastructure.http.controllers;

import com.fiap.mechanical_hub.application.command.serviceorder.CreateServiceOrderCommand;
import com.fiap.mechanical_hub.application.command.serviceorder.OpenServiceOrderCommand;
import com.fiap.mechanical_hub.application.dto.serviceorder.AddServicesToOrderRequest;
import com.fiap.mechanical_hub.application.dto.serviceorder.ServiceOrderDetailResponse;
import com.fiap.mechanical_hub.application.dto.serviceorder.ServiceOrderResponse;
import com.fiap.mechanical_hub.application.dto.serviceorder.ServiceOrderSummaryResponse;
import com.fiap.mechanical_hub.application.dto.serviceorder.UpdateStatusRequest;
import com.fiap.mechanical_hub.application.mappers.ServiceOrderMapper;
import com.fiap.mechanical_hub.application.usecases.serviceorder.AddTaskIntoServiceOrderUseCase;
import com.fiap.mechanical_hub.application.usecases.serviceorder.CreateServiceOrderUseCase;
import com.fiap.mechanical_hub.application.usecases.serviceorder.FindAllServiceOrderUseCase;
import com.fiap.mechanical_hub.application.usecases.serviceorder.FindServiceOrderByIdUseCase;
import com.fiap.mechanical_hub.application.usecases.serviceorder.OpenServiceOrderUseCase;
import com.fiap.mechanical_hub.application.usecases.serviceorder.UpdateServiceOrderStatusUseCase;
import com.fiap.mechanical_hub.application.usecases.serviceorder.UpdateTaskStatusUseCase;
import com.fiap.mechanical_hub.infrastructure.security.GatewayPrincipal;
import com.fiap.mechanical_hub.mocks.domain.entities.UserMock;
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

class ServiceOrderControllerTest {

    private static final UUID ORDER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID TASK_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");

    private final CreateServiceOrderUseCase createServiceOrderUseCase = mock(CreateServiceOrderUseCase.class);
    private final OpenServiceOrderUseCase openServiceOrderUseCase = mock(OpenServiceOrderUseCase.class);
    private final AddTaskIntoServiceOrderUseCase addTaskIntoServiceOrderUseCase = mock(AddTaskIntoServiceOrderUseCase.class);
    private final UpdateServiceOrderStatusUseCase updateServiceOrderStatusUseCase = mock(UpdateServiceOrderStatusUseCase.class);
    private final FindAllServiceOrderUseCase findAllServiceOrderUseCase = mock(FindAllServiceOrderUseCase.class);
    private final FindServiceOrderByIdUseCase findServiceOrderByIdUseCase = mock(FindServiceOrderByIdUseCase.class);
    private final UpdateTaskStatusUseCase updateTaskStatusUseCase = mock(UpdateTaskStatusUseCase.class);
    private final ServiceOrderMapper mapper = mock(ServiceOrderMapper.class);

    private final ServiceOrderController controller = new ServiceOrderController(
            createServiceOrderUseCase, openServiceOrderUseCase, addTaskIntoServiceOrderUseCase,
            updateServiceOrderStatusUseCase, findAllServiceOrderUseCase, findServiceOrderByIdUseCase,
            updateTaskStatusUseCase, mapper
    );

    private GatewayPrincipal buildUserDetails() {
        return new GatewayPrincipal(UserMock.USER_ID, "João Silva", "ADMINISTRATOR");
    }

    @Test
    void shouldReturnCreated_whenServiceOrderIsCreated() {
        when(mapper.toCreateServiceOrderCommand(any(), any())).thenReturn(mock(CreateServiceOrderCommand.class));
        when(createServiceOrderUseCase.execute(any())).thenReturn(mock(ServiceOrderResponse.class));

        ResponseEntity<ServiceOrderResponse> response = controller.create(mock(), buildUserDetails());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void shouldReturnCreated_whenServiceOrderIsOpened() {
        when(mapper.toOpenServiceOrderCommand(any(), any())).thenReturn(mock(OpenServiceOrderCommand.class));
        when(openServiceOrderUseCase.execute(any())).thenReturn(mock(ServiceOrderResponse.class));

        ResponseEntity<ServiceOrderResponse> response = controller.open(mock(), buildUserDetails());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void shouldReturnOk_whenUpdatingServiceOrderStatus() {
        UpdateStatusRequest request = new UpdateStatusRequest("EM_DIAGNOSTICO");

        ResponseEntity<Void> response = controller.updateStatus(ORDER_ID, request, buildUserDetails());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(updateServiceOrderStatusUseCase).execute(any());
    }

    @Test
    void shouldReturnOk_whenFindingAllServiceOrders() {
        when(findAllServiceOrderUseCase.execute()).thenReturn(List.of(mock(ServiceOrderSummaryResponse.class)));

        ResponseEntity<List<ServiceOrderSummaryResponse>> response = controller.findAll();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void shouldReturnOk_whenFindingServiceOrderById() {
        when(findServiceOrderByIdUseCase.execute(ORDER_ID)).thenReturn(mock(ServiceOrderDetailResponse.class));

        ResponseEntity<ServiceOrderDetailResponse> response = controller.findById(ORDER_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldReturnNoContent_whenAddingServicesToOrder() {
        AddServicesToOrderRequest request = new AddServicesToOrderRequest(List.of(UUID.randomUUID()));

        ResponseEntity<Void> response = controller.addServices(ORDER_ID, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(addTaskIntoServiceOrderUseCase).execute(any());
    }

    @Test
    void shouldReturnNoContent_whenUpdatingTaskStatus() {
        UpdateStatusRequest request = new UpdateStatusRequest("INICIADO");

        ResponseEntity<Void> response = controller.updateTaskStatus(ORDER_ID, TASK_ID, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(updateTaskStatusUseCase).execute(any());
    }
}
