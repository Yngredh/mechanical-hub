package com.fiap.mechanical_hub.infrastructure.database.repositories.adapter;

import com.fiap.mechanical_hub.domain.entities.Customer;
import com.fiap.mechanical_hub.domain.enums.DocumentTypeEnum;
import com.fiap.mechanical_hub.infrastructure.database.models.CustomerModel;
import com.fiap.mechanical_hub.infrastructure.database.repositories.CustomerJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerRepositoryAdapter")
class CustomerRepositoryAdapterTest {

    @Mock
    private CustomerJpaRepository jpaRepository;

    @InjectMocks
    private CustomerRepositoryAdapter repositoryAdapter;

    private UUID customerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Customer customer;
    private CustomerModel customerModel;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        createdAt = LocalDateTime.of(2026, 5, 1, 9, 0);
        updatedAt = LocalDateTime.of(2026, 5, 1, 10, 0);

        customer = new Customer(
                customerId,
                "Joao da Silva",
                DocumentTypeEnum.CPF,
                "52998224725",
                "11999990000",
                "joao@email.com",
                "Rua das Flores, 123",
                createdAt,
                updatedAt
        );

        customerModel = new CustomerModel(
                customerId,
                "Joao da Silva",
                DocumentTypeEnum.CPF,
                "52998224725",
                "11999990000",
                "joao@email.com",
                "Rua das Flores, 123",
                createdAt,
                updatedAt
        );
    }

    @Test
    @DisplayName("save should persist and map to domain")
    void saveShouldPersistAndMapToDomain() {
        when(jpaRepository.save(org.mockito.ArgumentMatchers.any(CustomerModel.class)))
                .thenReturn(customerModel);

        Customer result = repositoryAdapter.save(customer);

        assertThat(result).usingRecursiveComparison().isEqualTo(customer);
        verify(jpaRepository).save(org.mockito.ArgumentMatchers.any(CustomerModel.class));
    }

    @Test
    @DisplayName("findById should return mapped domain when found")
    void findByIdShouldReturnMappedDomain() {
        when(jpaRepository.findById(customerId)).thenReturn(Optional.of(customerModel));

        Optional<Customer> result = repositoryAdapter.findById(customerId);

        assertThat(result).isPresent();
        assertThat(result.get()).usingRecursiveComparison().isEqualTo(customer);
        verify(jpaRepository).findById(customerId);
    }

    @Test
    @DisplayName("findByDocumentNumber should return mapped domain when found")
    void findByDocumentNumberShouldReturnMappedDomain() {
        when(jpaRepository.findByDocumentNumber("52998224725")).thenReturn(Optional.of(customerModel));

        Optional<Customer> result = repositoryAdapter.findByDocumentNumber("52998224725");

        assertThat(result).isPresent();
        assertThat(result.get()).usingRecursiveComparison().isEqualTo(customer);
        verify(jpaRepository).findByDocumentNumber("52998224725");
    }

    @Test
    @DisplayName("findAll should map all entities")
    void findAllShouldMapAllEntities() {
        when(jpaRepository.findAll()).thenReturn(List.of(customerModel));

        List<Customer> result = repositoryAdapter.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).usingRecursiveComparison().isEqualTo(customer);
        verify(jpaRepository).findAll();
    }

    @Test
    @DisplayName("deleteById should delegate to JPA repository")
    void deleteByIdShouldDelegateToJpaRepository() {
        repositoryAdapter.deleteById(customerId);

        verify(jpaRepository).deleteById(customerId);
    }

    @Test
    @DisplayName("existsByDocumentNumber should return JPA result")
    void existsByDocumentNumberShouldReturnJpaResult() {
        when(jpaRepository.existsByDocumentNumber("52998224725")).thenReturn(true);

        boolean result = repositoryAdapter.existsByDocumentNumber("52998224725");

        assertThat(result).isTrue();
        verify(jpaRepository).existsByDocumentNumber("52998224725");
    }

    @Test
    @DisplayName("existsByDocumentNumberAndIdNot should return JPA result")
    void existsByDocumentNumberAndIdNotShouldReturnJpaResult() {
        when(jpaRepository.existsByDocumentNumberAndIdNot("52998224725", customerId)).thenReturn(true);

        boolean result = repositoryAdapter.existsByDocumentNumberAndIdNot("52998224725", customerId);

        assertThat(result).isTrue();
        verify(jpaRepository).existsByDocumentNumberAndIdNot("52998224725", customerId);
    }
}

