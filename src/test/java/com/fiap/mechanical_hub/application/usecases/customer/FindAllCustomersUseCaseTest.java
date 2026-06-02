package com.fiap.mechanical_hub.application.usecases.customer;

import com.fiap.mechanical_hub.mocks.domain.entities.CustomerMock;
import com.fiap.mechanical_hub.application.dto.customer.CustomerResponse;
import com.fiap.mechanical_hub.domain.entities.Customer;
import com.fiap.mechanical_hub.domain.repositories.CustomerRepository;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FindAllCustomersUseCaseTest {

    private final CustomerRepository customerRepository = mock(CustomerRepository.class);
    private final FindAllCustomersUseCase useCase = new FindAllCustomersUseCase(customerRepository);

    @Test
    void shouldReturnAllCustomers_whenRepositoryHasCustomers() {
        Customer customer1 = CustomerMock.withDefaultValues();
        Customer customer2 = CustomerMock.withName("Maria Silva");
        List<Customer> customers = List.of(customer1, customer2);

        when(customerRepository.findAll()).thenReturn(customers);

        List<CustomerResponse> result = useCase.execute();

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(2);
    }

    @Test
    void shouldReturnEmptyList_whenNoCustomersExist() {
        List<Customer> customers = new ArrayList<>();

        when(customerRepository.findAll()).thenReturn(customers);

        List<CustomerResponse> result = useCase.execute();

        assertThat(result).isEmpty();
    }

    @Test
    void shouldCallFindAllOnRepository() {
        Customer customer = CustomerMock.withDefaultValues();
        List<Customer> customers = List.of(customer);

        when(customerRepository.findAll()).thenReturn(customers);

        useCase.execute();

        verify(customerRepository).findAll();
    }

    @Test
    void shouldMapCustomersToResponse() {
        Customer customer = CustomerMock.withDefaultValues();
        List<Customer> customers = List.of(customer);

        when(customerRepository.findAll()).thenReturn(customers);

        List<CustomerResponse> result = useCase.execute();

        assertThat(result).isNotNull();
        assertThat(result.get(0)).isNotNull();
    }

    @Test
    void shouldReturnMultipleCustomers_inCorrectOrder() {
        Customer customer1 = CustomerMock.withName("Alice");
        Customer customer2 = CustomerMock.withName("Bruno");
        Customer customer3 = CustomerMock.withName("Carlos");
        List<Customer> customers = List.of(customer1, customer2, customer3);

        when(customerRepository.findAll()).thenReturn(customers);

        List<CustomerResponse> result = useCase.execute();

        assertThat(result).hasSize(3);
    }
}

