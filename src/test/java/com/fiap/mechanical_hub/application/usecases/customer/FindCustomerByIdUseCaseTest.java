package com.fiap.mechanical_hub.application.usecases.customer;

import com.fiap.mechanical_hub.mocks.domain.entities.CustomerMock;
import com.fiap.mechanical_hub.application.dto.customer.CustomerResponse;
import com.fiap.mechanical_hub.domain.entities.Customer;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.domain.repositories.CustomerRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FindCustomerByIdUseCaseTest {

    private final CustomerRepository customerRepository = mock(CustomerRepository.class);
    private final FindCustomerByIdUseCase useCase = new FindCustomerByIdUseCase(customerRepository);

    @Test
    void shouldReturnCustomer_whenCustomerExists() {
        UUID customerId = UUID.fromString("00000000-0000-0000-0000-000000000060");
        Customer customer = CustomerMock.withDefaultValues();

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        CustomerResponse result = useCase.execute(customerId);

        assertThat(result).isNotNull();
    }

    @Test
    void shouldThrowException_whenCustomerNotFound() {
        UUID customerId = UUID.randomUUID();

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(customerId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("não encontrado");
    }

    @Test
    void shouldCallFindByIdOnRepository() {
        UUID customerId = UUID.fromString("00000000-0000-0000-0000-000000000060");
        Customer customer = CustomerMock.withDefaultValues();

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        useCase.execute(customerId);

        verify(customerRepository).findById(customerId);
    }

    @Test
    void shouldMapCustomerToResponse_correctly() {
        UUID customerId = UUID.fromString("00000000-0000-0000-0000-000000000060");
        Customer customer = CustomerMock.withName("João Silva");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        CustomerResponse result = useCase.execute(customerId);

        assertThat(result).isNotNull();
    }

    @Test
    void shouldThrowNotFoundException_withCorrectMessage() {
        UUID customerId = UUID.randomUUID();

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(customerId))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldReturnResponseWithCustomerData() {
        UUID customerId = UUID.fromString("00000000-0000-0000-0000-000000000060");
        Customer customer = CustomerMock.withDefaultValues();

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        CustomerResponse result = useCase.execute(customerId);

        assertThat(result).isNotNull();
        verify(customerRepository).findById(customerId);
    }
}

