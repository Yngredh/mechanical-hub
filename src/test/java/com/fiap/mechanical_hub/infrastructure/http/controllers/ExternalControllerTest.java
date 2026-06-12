package com.fiap.mechanical_hub.infrastructure.http.controllers;

import com.fiap.mechanical_hub.application.dto.serviceorder.request.ServiceOrderCustomerView;
import com.fiap.mechanical_hub.application.usecases.serviceorder.ApproveServiceOrderUseCase;
import com.fiap.mechanical_hub.application.usecases.serviceorder.FindByOrderNumberUseCase;
import com.fiap.mechanical_hub.application.usecases.serviceorder.RejectServiceOrderUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExternalControllerTest {

    private static final UUID ORDER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final String ORDER_NUMBER = "OS-001";

    private final ApproveServiceOrderUseCase approveServiceOrderUseCase = mock(ApproveServiceOrderUseCase.class);
    private final RejectServiceOrderUseCase rejectServiceOrderUseCase = mock(RejectServiceOrderUseCase.class);
    private final FindByOrderNumberUseCase findByOrderNumberUseCase = mock(FindByOrderNumberUseCase.class);

    private final ExternalController controller = new ExternalController(
            approveServiceOrderUseCase, rejectServiceOrderUseCase, findByOrderNumberUseCase
    );

    @Test
    void shouldReturnNoContent_whenApprovingServiceOrder() {
        ResponseEntity<Void> response = controller.approve(ORDER_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(approveServiceOrderUseCase).execute(any());
    }

    @Test
    void shouldReturnNoContent_whenRejectingServiceOrder() {
        ResponseEntity<Void> response = controller.reject(ORDER_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(rejectServiceOrderUseCase).execute(any());
    }

    @Test
    void shouldReturnOk_whenFindingServiceOrderByNumber() {
        when(findByOrderNumberUseCase.execute(any())).thenReturn(mock(ServiceOrderCustomerView.class));

        ResponseEntity<ServiceOrderCustomerView> response = controller.findByOrderNumber(ORDER_NUMBER);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void shouldDelegateToFindByOrderNumberUseCase_whenFindingByOrderNumber() {
        when(findByOrderNumberUseCase.execute(any())).thenReturn(mock(ServiceOrderCustomerView.class));

        controller.findByOrderNumber(ORDER_NUMBER);

        verify(findByOrderNumberUseCase).execute(any());
    }
}
