package com.fiap.mechanical_hub.application.usecases.serviceorder;

import com.fiap.mechanical_hub.application.command.serviceorder.OpenServiceOrderCommand;
import com.fiap.mechanical_hub.application.dto.serviceorder.ServiceOrderResponse;
import com.fiap.mechanical_hub.application.mappers.ServiceOrderMapper;
import com.fiap.mechanical_hub.domain.exceptions.BusinessRuleException;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.domain.repositories.CustomerRepository;
import com.fiap.mechanical_hub.domain.repositories.ServiceOrderRepository;
import com.fiap.mechanical_hub.domain.repositories.VehicleRepository;
import com.fiap.mechanical_hub.domain.utils.OrderNumberGenerator;
import com.fiap.mechanical_hub.mocks.domain.entities.CustomerMock;
import com.fiap.mechanical_hub.mocks.domain.entities.VehicleMock;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import com.fiap.mechanical_hub.application.command.serviceorder.AddTaskIntoServiceOrderCommand;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OpenServiceOrderUseCaseTest {

    private static final UUID CUSTOMER_ID = UUID.fromString("00000000-0000-0000-0000-000000000060");
    private static final UUID VEHICLE_ID = UUID.fromString("00000000-0000-0000-0000-000000000070");
    private static final UUID USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000004");

    private final ServiceOrderRepository serviceOrderRepository = mock(ServiceOrderRepository.class);
    private final CustomerRepository customerRepository = mock(CustomerRepository.class);
    private final VehicleRepository vehicleRepository = mock(VehicleRepository.class);
    private final AddTaskIntoServiceOrderUseCase addTaskIntoServiceOrderUseCase = mock(AddTaskIntoServiceOrderUseCase.class);
    private final OrderNumberGenerator orderNumberGenerator = mock(OrderNumberGenerator.class);
    private final ServiceOrderMapper mapper = mock(ServiceOrderMapper.class);
    private final OpenServiceOrderUseCase useCase = new OpenServiceOrderUseCase(
            serviceOrderRepository, customerRepository, vehicleRepository,
            addTaskIntoServiceOrderUseCase, orderNumberGenerator, mapper);

    @Test
    void shouldOpenOrderAndReturnResponse_whenCustomerAndVehicleBelongTogether() {
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(CustomerMock.withDefaultValues()));
        when(vehicleRepository.findById(VEHICLE_ID)).thenReturn(Optional.of(VehicleMock.withDefaultValues()));
        when(orderNumberGenerator.generate()).thenReturn("OS-001");
        when(serviceOrderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toResponse(any())).thenReturn(mock(ServiceOrderResponse.class));
        OpenServiceOrderCommand command = new OpenServiceOrderCommand(CUSTOMER_ID, VEHICLE_ID, List.of(), "Diagnóstico", USER_ID);

        ServiceOrderResponse result = useCase.execute(command);

        assertThat(result).isNotNull();
    }

    @Test
    void shouldThrowNotFoundException_whenCustomerDoesNotExist() {
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.empty());
        OpenServiceOrderCommand command = new OpenServiceOrderCommand(CUSTOMER_ID, VEHICLE_ID, List.of(), "Diagnóstico", USER_ID);

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(CUSTOMER_ID.toString());
    }

    @Test
    void shouldThrowNotFoundException_whenVehicleDoesNotExist() {
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(CustomerMock.withDefaultValues()));
        when(vehicleRepository.findById(VEHICLE_ID)).thenReturn(Optional.empty());
        OpenServiceOrderCommand command = new OpenServiceOrderCommand(CUSTOMER_ID, VEHICLE_ID, List.of(), "Diagnóstico", USER_ID);

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(VEHICLE_ID.toString());
    }

    @Test
    void shouldThrowBusinessRuleException_whenVehicleDoesNotBelongToCustomer() {
        UUID otherCustomerId = UUID.fromString("00000000-0000-0000-0000-000000000099");
        when(customerRepository.findById(otherCustomerId)).thenReturn(Optional.of(CustomerMock.withDefaultValues()));
        when(vehicleRepository.findById(VEHICLE_ID)).thenReturn(Optional.of(VehicleMock.withDefaultValues()));
        OpenServiceOrderCommand command = new OpenServiceOrderCommand(otherCustomerId, VEHICLE_ID, List.of(), "Diagnóstico", USER_ID);

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void shouldAddServicesAndReturnResponse_whenCommandHasServiceIds() {
        UUID serviceId = UUID.fromString("00000000-0000-0000-0000-000000000010");
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(CustomerMock.withDefaultValues()));
        when(vehicleRepository.findById(VEHICLE_ID)).thenReturn(Optional.of(VehicleMock.withDefaultValues()));
        when(orderNumberGenerator.generate()).thenReturn("OS-002");
        when(serviceOrderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(serviceOrderRepository.findById(any())).thenAnswer(inv -> {
            com.fiap.mechanical_hub.domain.entities.ServiceOrder order = com.fiap.mechanical_hub.mocks.domain.entities.ServiceOrderMock.inDiagnosis();
            return Optional.of(order);
        });
        when(mapper.toResponse(any())).thenReturn(mock(ServiceOrderResponse.class));
        OpenServiceOrderCommand command = new OpenServiceOrderCommand(CUSTOMER_ID, VEHICLE_ID, List.of(serviceId), "Diagnóstico", USER_ID);

        ServiceOrderResponse result = useCase.execute(command);

        assertThat(result).isNotNull();
        verify(addTaskIntoServiceOrderUseCase).execute(any(AddTaskIntoServiceOrderCommand.class));
    }
}
