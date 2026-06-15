package com.fiap.mechanical_hub.application.usecases.service;

import com.fiap.mechanical_hub.application.command.ordertask.DeleteOrderTaskCommand;
import com.fiap.mechanical_hub.domain.entities.OrderTask;
import com.fiap.mechanical_hub.domain.entities.ServiceData;
import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.domain.exceptions.BusinessRuleException;
import com.fiap.mechanical_hub.domain.exceptions.ServiceNotFoundException;
import com.fiap.mechanical_hub.domain.repositories.OrderTaskRepository;
import com.fiap.mechanical_hub.domain.repositories.ServiceOrderRepository;
import com.fiap.mechanical_hub.domain.repositories.ServiceRepository;
import com.fiap.mechanical_hub.domain.service.ServiceOrderDomainService;
import com.fiap.mechanical_hub.mocks.domain.entities.OrderTaskMock;
import com.fiap.mechanical_hub.mocks.domain.entities.ServiceDataMock;
import com.fiap.mechanical_hub.mocks.domain.entities.ServiceOrderMock;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DeleteServiceUseCaseTest {

    private static final UUID SERVICE_ID = UUID.fromString("00000000-0000-0000-0000-000000000040");

    private final ServiceRepository serviceRepository = mock(ServiceRepository.class);
    private final OrderTaskRepository orderTaskRepository = mock(OrderTaskRepository.class);
    private final ServiceOrderRepository serviceOrderRepository = mock(ServiceOrderRepository.class);
    private final ServiceOrderDomainService orderDomainService = mock(ServiceOrderDomainService.class);
    private final DeleteServiceUseCase useCase = new DeleteServiceUseCase(
            serviceRepository, orderTaskRepository, serviceOrderRepository, orderDomainService
    );

    @Test
    void shouldDeleteService_whenServiceExistsAndHasNoOpenOrders() {
        ServiceData service = ServiceDataMock.withDefaultValues();
        DeleteOrderTaskCommand command = new DeleteOrderTaskCommand(SERVICE_ID);

        when(serviceRepository.findById(SERVICE_ID)).thenReturn(Optional.of(service));
        when(orderTaskRepository.findAllByServiceId(SERVICE_ID)).thenReturn(List.of());
        when(serviceOrderRepository.findAllIn(List.of())).thenReturn(List.of());

        assertThatCode(() -> useCase.execute(command)).doesNotThrowAnyException();
    }

    @Test
    void shouldThrowServiceNotFoundException_whenServiceDoesNotExist() {
        UUID unknownServiceId = UUID.fromString("00000000-0000-0000-0000-000000000099");
        DeleteOrderTaskCommand command = new DeleteOrderTaskCommand(unknownServiceId);

        when(serviceRepository.findById(unknownServiceId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(ServiceNotFoundException.class);
    }

    @Test
    void shouldThrowException_whenServiceHasOpenOrders() {
        ServiceData service = ServiceDataMock.withDefaultValues();
        OrderTask task = OrderTaskMock.notStarted();
        ServiceOrder openOrder = ServiceOrderMock.inProgress();
        DeleteOrderTaskCommand command = new DeleteOrderTaskCommand(SERVICE_ID);

        when(serviceRepository.findById(SERVICE_ID)).thenReturn(Optional.of(service));
        when(orderTaskRepository.findAllByServiceId(SERVICE_ID)).thenReturn(List.of(task));
        when(serviceOrderRepository.findAllIn(any())).thenReturn(List.of(openOrder));
        org.mockito.Mockito.doThrow(new BusinessRuleException("Essa ação não pode ser executada pois há ordens abertas"))
                .when(orderDomainService).hasAnyOpenServiceOrder(List.of(openOrder));

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("ordens abertas");
    }

    @Test
    void shouldCallRepositoryDeleteById_whenDeleteSucceeds() {
        ServiceData service = ServiceDataMock.withDefaultValues();
        DeleteOrderTaskCommand command = new DeleteOrderTaskCommand(SERVICE_ID);

        when(serviceRepository.findById(SERVICE_ID)).thenReturn(Optional.of(service));
        when(orderTaskRepository.findAllByServiceId(SERVICE_ID)).thenReturn(List.of());
        when(serviceOrderRepository.findAllIn(List.of())).thenReturn(List.of());

        useCase.execute(command);

        verify(serviceRepository).deleteById(service.getId());
    }

    @Test
    void shouldRetrieveTasksForService_beforeCheckingOrders() {
        ServiceData service = ServiceDataMock.withDefaultValues();
        DeleteOrderTaskCommand command = new DeleteOrderTaskCommand(SERVICE_ID);

        when(serviceRepository.findById(SERVICE_ID)).thenReturn(Optional.of(service));
        when(orderTaskRepository.findAllByServiceId(SERVICE_ID)).thenReturn(List.of());
        when(serviceOrderRepository.findAllIn(List.of())).thenReturn(List.of());

        useCase.execute(command);

        verify(orderTaskRepository).findAllByServiceId(SERVICE_ID);
    }
}
