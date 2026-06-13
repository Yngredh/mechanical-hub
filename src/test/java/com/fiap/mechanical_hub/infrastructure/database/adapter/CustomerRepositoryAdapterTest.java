package com.fiap.mechanical_hub.infrastructure.database.adapter;

import com.fiap.mechanical_hub.domain.entities.Customer;
import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.mocks.domain.entities.CustomerMock;
import com.fiap.mechanical_hub.mocks.infrastructure.database.models.CustomerModelMock;
import com.fiap.mechanical_hub.mocks.infrastructure.database.models.ServiceOrderModelMock;
import com.fiap.mechanical_hub.infrastructure.database.repositories.CustomerJpaRepository;
import com.fiap.mechanical_hub.infrastructure.database.repositories.ServiceOrderJpaRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CustomerRepositoryAdapterTest {

    private final CustomerJpaRepository jpaRepository = mock(CustomerJpaRepository.class);
    private final ServiceOrderJpaRepository serviceOrderJpaRepository = mock(ServiceOrderJpaRepository.class);

    private final CustomerRepositoryAdapter adapter = new CustomerRepositoryAdapter(
            jpaRepository, serviceOrderJpaRepository
    );

    @Test
    void shouldReturnSavedCustomer_whenSavingCustomer() {
        when(jpaRepository.save(any())).thenReturn(CustomerModelMock.withDefaultValues());

        Customer result = adapter.save(CustomerMock.withDefaultValues());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(CustomerModelMock.CUSTOMER_ID);
    }

    @Test
    void shouldDelegateToJpaRepository_whenSavingCustomer() {
        when(jpaRepository.save(any())).thenReturn(CustomerModelMock.withDefaultValues());

        adapter.save(CustomerMock.withDefaultValues());

        verify(jpaRepository).save(any());
    }

    @Test
    void shouldReturnCustomer_whenFindByIdAndCustomerExists() {
        when(jpaRepository.findByIdAndDeletedAtIsNull(CustomerModelMock.CUSTOMER_ID))
                .thenReturn(Optional.of(CustomerModelMock.withDefaultValues()));

        Optional<Customer> result = adapter.findById(CustomerModelMock.CUSTOMER_ID);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(CustomerModelMock.CUSTOMER_ID);
    }

    @Test
    void shouldReturnEmpty_whenFindByIdAndCustomerDoesNotExist() {
        when(jpaRepository.findByIdAndDeletedAtIsNull(CustomerModelMock.CUSTOMER_ID))
                .thenReturn(Optional.empty());

        Optional<Customer> result = adapter.findById(CustomerModelMock.CUSTOMER_ID);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnCustomer_whenFindByDocumentNumberAndExists() {
        when(jpaRepository.findByDocumentNumber("52998224725"))
                .thenReturn(Optional.of(CustomerModelMock.withDefaultValues()));

        Optional<Customer> result = adapter.findByDocumentNumber("52998224725");

        assertThat(result).isPresent();
    }

    @Test
    void shouldReturnEmpty_whenFindByDocumentNumberAndNotExists() {
        when(jpaRepository.findByDocumentNumber("52998224725")).thenReturn(Optional.empty());

        Optional<Customer> result = adapter.findByDocumentNumber("52998224725");

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnAllCustomers_whenFindAll() {
        when(jpaRepository.findByDeletedAtIsNull())
                .thenReturn(List.of(CustomerModelMock.withDefaultValues()));

        List<Customer> result = adapter.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(CustomerModelMock.CUSTOMER_ID);
    }

    @Test
    void shouldDelegateToJpaRepository_whenDeletingById() {
        adapter.deleteById(CustomerModelMock.CUSTOMER_ID);

        verify(jpaRepository).deleteById(CustomerModelMock.CUSTOMER_ID);
    }

    @Test
    void shouldReturnTrue_whenDocumentNumberExists() {
        when(jpaRepository.existsByDocumentNumber("52998224725")).thenReturn(true);

        boolean result = adapter.existsByDocumentNumber("52998224725");

        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalse_whenDocumentNumberDoesNotExist() {
        when(jpaRepository.existsByDocumentNumber("52998224725")).thenReturn(false);

        boolean result = adapter.existsByDocumentNumber("52998224725");

        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnOrders_whenFindOrdersByCustomerId() {
        when(serviceOrderJpaRepository.findAllOpenOrdersByCustomerId(CustomerModelMock.CUSTOMER_ID))
                .thenReturn(List.of(ServiceOrderModelMock.received()));

        List<ServiceOrder> result = adapter.findOrdersByCustomerId(CustomerModelMock.CUSTOMER_ID);

        assertThat(result).hasSize(1);
    }
}
