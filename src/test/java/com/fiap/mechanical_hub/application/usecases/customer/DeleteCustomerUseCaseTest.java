package com.fiap.mechanical_hub.application.usecases.customer;

import com.fiap.mechanical_hub.mocks.domain.entities.CustomerMock;
import com.fiap.mechanical_hub.mocks.domain.entities.ServiceOrderMock;
import com.fiap.mechanical_hub.application.usecases.vehicle.DeleteVehicleUseCase;
import com.fiap.mechanical_hub.domain.entities.Customer;
import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.domain.exceptions.BusinessRuleException;
import com.fiap.mechanical_hub.domain.exceptions.CustomerNotFoundException;
import com.fiap.mechanical_hub.domain.repositories.CustomerRepository;
import com.fiap.mechanical_hub.domain.service.ServiceOrderDomainService;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DeleteCustomerUseCaseTest {

    private final CustomerRepository customerRepository = mock(CustomerRepository.class);
    private final DeleteVehicleUseCase deleteVehicleUseCase = mock(DeleteVehicleUseCase.class);
    private final ServiceOrderDomainService orderDomainService = mock(ServiceOrderDomainService.class);
    private final DeleteCustomerUseCase useCase = new DeleteCustomerUseCase(
            customerRepository,
            deleteVehicleUseCase,
            orderDomainService
    );

    @Test
    void shouldDeleteCustomer_whenCustomerExistsAndHasNoOpenOrders() {
        UUID customerId = UUID.fromString("00000000-0000-0000-0000-000000000060");
        Customer customer = CustomerMock.withDefaultValues();
        List<ServiceOrder> orders = new ArrayList<>();
        orders.add(ServiceOrderMock.finished());

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(customerRepository.findOrdersByCustomerId(customerId)).thenReturn(orders);

        assertThatCode(() -> useCase.execute(customerId))
                .doesNotThrowAnyException();

        verify(customerRepository).findById(customerId);
        verify(deleteVehicleUseCase).execute(customerId);
        verify(customerRepository).save(customer);
    }

    @Test
    void shouldThrowException_whenCustomerNotFound() {
        UUID customerId = UUID.randomUUID();

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(customerId))
                .isInstanceOf(CustomerNotFoundException.class);
    }

    @Test
    void shouldThrowException_whenCustomerHasOpenOrders() {
        UUID customerId = UUID.fromString("00000000-0000-0000-0000-000000000060");
        Customer customer = CustomerMock.withDefaultValues();
        List<ServiceOrder> orders = new ArrayList<>();
        orders.add(ServiceOrderMock.inProgress());

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(customerRepository.findOrdersByCustomerId(customerId)).thenReturn(orders);
        
        doThrow(new BusinessRuleException("Há ordens abertas"))
                .when(orderDomainService).hasAnyOpenServiceOrder(orders);

        assertThatThrownBy(() -> useCase.execute(customerId))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("ordens abertas");
    }

    @Test
    void shouldCallDeleteVehicleUseCase_beforeDeactivatingCustomer() {
        UUID customerId = UUID.fromString("00000000-0000-0000-0000-000000000060");
        Customer customer = CustomerMock.withDefaultValues();
        List<ServiceOrder> orders = new ArrayList<>();

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(customerRepository.findOrdersByCustomerId(customerId)).thenReturn(orders);

        useCase.execute(customerId);

        verify(deleteVehicleUseCase).execute(customerId);
    }

    @Test
    void shouldDeactivateCustomer_beforeSaving() {
        UUID customerId = UUID.fromString("00000000-0000-0000-0000-000000000060");
        Customer customer = CustomerMock.withDefaultValues();
        List<ServiceOrder> orders = new ArrayList<>();

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(customerRepository.findOrdersByCustomerId(customerId)).thenReturn(orders);

        useCase.execute(customerId);

        verify(customerRepository).save(customer);
    }

    @Test
    void shouldValidateOrdersBeforeProcessing() {
        UUID customerId = UUID.fromString("00000000-0000-0000-0000-000000000060");
        Customer customer = CustomerMock.withDefaultValues();
        List<ServiceOrder> orders = new ArrayList<>();
        orders.add(ServiceOrderMock.finished());

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(customerRepository.findOrdersByCustomerId(customerId)).thenReturn(orders);

        useCase.execute(customerId);

        verify(orderDomainService).hasAnyOpenServiceOrder(orders);
    }
}

