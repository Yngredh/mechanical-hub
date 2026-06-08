package com.fiap.mechanical_hub.application.usecases.customer;

import com.fiap.mechanical_hub.mocks.domain.entities.CustomerMock;
import com.fiap.mechanical_hub.application.command.customer.UpdateCustomerCommand;
import com.fiap.mechanical_hub.domain.entities.Customer;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.domain.repositories.CustomerRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UpdateCustomerUseCaseTest {

    private final CustomerRepository customerRepository = mock(CustomerRepository.class);
    private final UpdateCustomerUseCase useCase = new UpdateCustomerUseCase(customerRepository);

    @Test
    void shouldUpdateCustomer_whenCustomerExists() {
        UUID customerId = UUID.fromString("00000000-0000-0000-0000-000000000060");
        UpdateCustomerCommand command = new UpdateCustomerCommand(
                customerId,
                "João Silva Updated",
                "5511987654321",
                "joao.updated@email.com",
                "Rua B, 456"
        );
        Customer existingCustomer = CustomerMock.withDefaultValues();
        Customer updatedCustomer = CustomerMock.withName("João Silva Updated");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(updatedCustomer);

        Customer result = useCase.execute(command);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("João Silva Updated");
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void shouldThrowException_whenCustomerNotFound() {
        UUID customerId = UUID.randomUUID();
        UpdateCustomerCommand command = new UpdateCustomerCommand(
                customerId,
                "João Silva Updated",
                "5511987654321",
                "joao.updated@email.com",
                "Rua B, 456"
        );

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("não encontrado");
    }

    @Test
    void shouldCallFindByIdOnRepository() {
        UUID customerId = UUID.fromString("00000000-0000-0000-0000-000000000060");
        UpdateCustomerCommand command = new UpdateCustomerCommand(
                customerId,
                "João Silva Updated",
                "5511987654321",
                "joao.updated@email.com",
                "Rua B, 456"
        );
        Customer existingCustomer = CustomerMock.withDefaultValues();
        Customer updatedCustomer = CustomerMock.withName("João Silva Updated");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(updatedCustomer);

        useCase.execute(command);

        verify(customerRepository).findById(customerId);
    }

    @Test
    void shouldUpdateAllFields_fromCommand() {
        UUID customerId = UUID.fromString("00000000-0000-0000-0000-000000000060");
        UpdateCustomerCommand command = new UpdateCustomerCommand(
                customerId,
                "Maria Santos",
                "5511955555555",
                "maria@email.com",
                "Avenida C, 789"
        );
        Customer existingCustomer = CustomerMock.withDefaultValues();
        Customer updatedCustomer = CustomerMock.withName("Maria Santos");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(updatedCustomer);

        Customer result = useCase.execute(command);

        assertThat(result.getName()).isEqualTo("Maria Santos");
    }

    @Test
    void shouldSaveUpdatedCustomerToRepository() {
        UUID customerId = UUID.fromString("00000000-0000-0000-0000-000000000060");
        UpdateCustomerCommand command = new UpdateCustomerCommand(
                customerId,
                "João Silva Updated",
                "5511987654321",
                "joao.updated@email.com",
                "Rua B, 456"
        );
        Customer existingCustomer = CustomerMock.withDefaultValues();
        Customer updatedCustomer = CustomerMock.withName("João Silva Updated");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(updatedCustomer);

        useCase.execute(command);

        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void shouldReturnUpdatedCustomer_afterSaving() {
        UUID customerId = UUID.fromString("00000000-0000-0000-0000-000000000060");
        UpdateCustomerCommand command = new UpdateCustomerCommand(
                customerId,
                "Pedro Costa",
                "5511966666666",
                "pedro@email.com",
                "Rua E, 111"
        );
        Customer existingCustomer = CustomerMock.withDefaultValues();
        Customer updatedCustomer = CustomerMock.withName("Pedro Costa");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(updatedCustomer);

        Customer result = useCase.execute(command);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Pedro Costa");
    }
}

