package com.fiap.mechanical_hub.application.usecases.serviceorder;

import com.fiap.mechanical_hub.application.command.serviceorder.AddTaskIntoServiceOrderCommand;
import com.fiap.mechanical_hub.application.command.stock.ReserveStockForServiceOrderCommand;
import com.fiap.mechanical_hub.application.usecases.service.GetServiceByIdUseCase;
import com.fiap.mechanical_hub.application.usecases.stock.ReserveStockForServiceOrderUseCase;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.domain.repositories.ServiceMaterialRepository;
import com.fiap.mechanical_hub.domain.repositories.ServiceOrderRepository;
import com.fiap.mechanical_hub.mocks.domain.entities.ServiceDataMock;
import com.fiap.mechanical_hub.mocks.domain.entities.ServiceMaterialMock;
import com.fiap.mechanical_hub.mocks.domain.entities.ServiceOrderMock;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AddTaskIntoServiceOrderUseCaseTest {

    private static final UUID ORDER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID SERVICE_ID = UUID.fromString("00000000-0000-0000-0000-000000000040");

    private final GetServiceByIdUseCase getServiceByIdUseCase = mock(GetServiceByIdUseCase.class);
    private final ReserveStockForServiceOrderUseCase reserveStockForServiceOrderUseCase = mock(ReserveStockForServiceOrderUseCase.class);
    private final ServiceOrderRepository repository = mock(ServiceOrderRepository.class);
    private final ServiceMaterialRepository serviceMaterialRepository = mock(ServiceMaterialRepository.class);
    private final AddTaskIntoServiceOrderUseCase useCase = new AddTaskIntoServiceOrderUseCase(
            getServiceByIdUseCase, reserveStockForServiceOrderUseCase, repository, serviceMaterialRepository);

    @Test
    void shouldSaveOrderAfterAddingServices_whenOrderExists() {
        when(repository.findById(ORDER_ID)).thenReturn(Optional.of(ServiceOrderMock.inDiagnosis()));
        when(getServiceByIdUseCase.execute(SERVICE_ID)).thenReturn(ServiceDataMock.withDefaultValues());
        when(serviceMaterialRepository.findByServiceId(SERVICE_ID)).thenReturn(List.of(ServiceMaterialMock.withDefaultValues()));
        when(reserveStockForServiceOrderUseCase.execute(any(ReserveStockForServiceOrderCommand.class))).thenReturn(mock(com.fiap.mechanical_hub.domain.entities.Stock.class));

        useCase.execute(new AddTaskIntoServiceOrderCommand(ORDER_ID, List.of(SERVICE_ID)));

        verify(repository).save(any());
    }

    @Test
    void shouldSetHasStockPendingTrue_whenStockReservationReturnsNull() {
        when(repository.findById(ORDER_ID)).thenReturn(Optional.of(ServiceOrderMock.inDiagnosis()));
        when(getServiceByIdUseCase.execute(SERVICE_ID)).thenReturn(ServiceDataMock.withDefaultValues());
        when(serviceMaterialRepository.findByServiceId(SERVICE_ID)).thenReturn(List.of(ServiceMaterialMock.withDefaultValues()));
        when(reserveStockForServiceOrderUseCase.execute(any(ReserveStockForServiceOrderCommand.class))).thenReturn(null);

        useCase.execute(new AddTaskIntoServiceOrderCommand(ORDER_ID, List.of(SERVICE_ID)));

        verify(repository).save(any());
    }

    @Test
    void shouldThrowNotFoundException_whenOrderDoesNotExist() {
        when(repository.findById(ORDER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(new AddTaskIntoServiceOrderCommand(ORDER_ID, List.of(SERVICE_ID))))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(ORDER_ID.toString());
    }
}
