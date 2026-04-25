package com.fiap.mechanical_hub.application.usecases;

import com.fiap.mechanical_hub.application.dto.customer.UpsertCustomerRequest;
import com.fiap.mechanical_hub.application.dto.customer.CustomerResponse;
import com.fiap.mechanical_hub.application.mappers.CustomerMapper;
import com.fiap.mechanical_hub.domain.entities.Customer;
import com.fiap.mechanical_hub.domain.enums.DocumentType;
import com.fiap.mechanical_hub.domain.exceptions.DuplicateDocumentException;
import com.fiap.mechanical_hub.domain.exceptions.InvalidDocumentException;
import com.fiap.mechanical_hub.domain.exceptions.InvalidTelephoneException;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.application.repositories.CustomerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do CRUD de Clientes - CustomerService")
class CustomerUseCaseTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerUseCase customerUseCase;

    @Test
    @DisplayName("Deve criar cliente com CPF válido com sucesso")
    void shouldCreateCustomerWithValidCPF() {
        String validCPF = "123.456.789-09";
        UpsertCustomerRequest request = new UpsertCustomerRequest(
                "João Silva",
                "CPF",
                validCPF,
                "55(11) 98765-4321",
                "joao@example.com",
                "Rua A, 123"
        );

        Customer customerDomain = Customer.create(
                "João Silva",
                DocumentType.CPF,
                validCPF,
                "55(11) 98765-4321",
                "joao@example.com",
                "Rua A, 123"
        );

        Customer savedCustomer = new Customer(
                UUID.randomUUID(),
                "João Silva",
                DocumentType.CPF,
                "12345678909",
                "5511987654321",
                "joao@example.com",
                "Rua A, 123",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        CustomerResponse expectedResponse = new CustomerResponse(
                savedCustomer.getId(),
                savedCustomer.getName(),
                savedCustomer.getDocumentType().getValue(),
                "123.456.789-09",
                "55(11) 9 8765-4321",
                savedCustomer.getEmail(),
                savedCustomer.getAddress(),
                savedCustomer.getCreatedAt(),
                savedCustomer.getUpdatedAt()
        );

        when(customerRepository.existsByDocumentNumber("12345678909")).thenReturn(false);
        when(customerMapper.toDomainEntity(request)).thenReturn(customerDomain);
        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);
        when(customerMapper.toResponse(savedCustomer)).thenReturn(expectedResponse);

        CustomerResponse response = customerUseCase.create(request);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("João Silva");
        assertThat(response.getDocumentType()).isEqualTo("CPF");
        assertThat(response.getDocumentNumber()).isEqualTo("123.456.789-09");
        verify(customerRepository).existsByDocumentNumber("12345678909");
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    @DisplayName("Deve criar cliente com CNPJ válido com sucesso")
    void shouldCreateCustomerWithValidCNPJ() {
        String validCNPJ = "11.222.333/0001-81";
        UpsertCustomerRequest request = new UpsertCustomerRequest(
                "Empresa XYZ",
                "CNPJ",
                validCNPJ,
                "55 (11) 3456-7890",
                "empresa@example.com",
                "Av. B, 456"
        );

        Customer customerDomain = Customer.create(
                "Empresa XYZ",
                DocumentType.CNPJ,
                validCNPJ,
                "55 (11) 3456-7890",
                "empresa@example.com",
                "Av. B, 456"
        );

        Customer savedCustomer = new Customer(
                UUID.randomUUID(),
                "Empresa XYZ",
                DocumentType.CNPJ,
                "11222333000181",
                "55 1134567890",
                "empresa@example.com",
                "Av. B, 456",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        CustomerResponse expectedResponse = new CustomerResponse(
                savedCustomer.getId(),
                savedCustomer.getName(),
                savedCustomer.getDocumentType().getValue(),
                "11.222.333/0001-81",
                "55 (11) 3456-7890",
                savedCustomer.getEmail(),
                savedCustomer.getAddress(),
                savedCustomer.getCreatedAt(),
                savedCustomer.getUpdatedAt()
        );

        when(customerRepository.existsByDocumentNumber("11222333000181")).thenReturn(false);
        when(customerMapper.toDomainEntity(request)).thenReturn(customerDomain);
        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);
        when(customerMapper.toResponse(savedCustomer)).thenReturn(expectedResponse);

        CustomerResponse response = customerUseCase.create(request);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Empresa XYZ");
        assertThat(response.getDocumentType()).isEqualTo("CNPJ");
        assertThat(response.getDocumentNumber()).isEqualTo("11.222.333/0001-81");
        verify(customerRepository).existsByDocumentNumber("11222333000181");
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    @DisplayName("Não deve criar cliente com CPF inválido")
    void shouldThrowExceptionWhenCPFIsInvalid() {
        UpsertCustomerRequest request = new UpsertCustomerRequest(
                "João Silva",
                "CPF",
                "111.111.111-11",
                "(11) 98765-4321",
                "joao@example.com",
                "Rua A, 123"
        );

        when(customerMapper.toDomainEntity(request))
                .thenThrow(new InvalidDocumentException("Inválido CPF: 111.111.111-11"));

        assertThatThrownBy(() -> customerUseCase.create(request))
                .isInstanceOf(InvalidDocumentException.class)
                .hasMessageContaining("Inválido CPF");

        verify(customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Não deve criar cliente com CNPJ inválido")
    void shouldThrowExceptionWhenCNPJIsInvalid() {
        UpsertCustomerRequest request = new UpsertCustomerRequest(
                "Empresa XYZ",
                "CNPJ",
                "11.111.111/0001-81",
                "(11) 3456-7890",
                "empresa@example.com",
                "Av. B, 456"
        );

        when(customerMapper.toDomainEntity(request))
                .thenThrow(new InvalidDocumentException("Inválido CNPJ: 11.111.111/0001-81"));

        assertThatThrownBy(() -> customerUseCase.create(request))
                .isInstanceOf(InvalidDocumentException.class)
                .hasMessageContaining("Inválido CNPJ");

        verify(customerRepository, never()).save(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"123456789", "(11) 9876", "12", "", "123"})
    @DisplayName("Não deve criar cliente com telefone inválido")
    void shouldThrowExceptionWhenTelephoneIsInvalid(String invalidTelephone) {
        UpsertCustomerRequest request = new UpsertCustomerRequest(
                "João Silva",
                "CPF",
                "123.456.789-09",
                invalidTelephone,
                "joao@example.com",
                "Rua A, 123"
        );

        when(customerMapper.toDomainEntity(request))
                .thenThrow(new InvalidTelephoneException("Telefone inválido: após remover a formatação, deve conter pelo menos 12 dígitos"));

        assertThatThrownBy(() -> customerUseCase.create(request))
                .isInstanceOf(InvalidTelephoneException.class)
                .hasMessageContaining("inválido");

        verify(customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Não deve criar cliente com documento duplicado (CPF)")
    void shouldThrowExceptionWhenCPFAlreadyExists() {
        UpsertCustomerRequest request = new UpsertCustomerRequest(
                "João Silva",
                "CPF",
                "123.456.789-09",
                "(11) 98765-4321",
                "joao@example.com",
                "Rua A, 123"
        );

        when(customerRepository.existsByDocumentNumber("12345678909")).thenReturn(true);

        assertThatThrownBy(() -> customerUseCase.create(request))
                .isInstanceOf(DuplicateDocumentException.class)
                .hasMessageContaining("já existe");

        verify(customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Não deve criar cliente com documento duplicado (CNPJ)")
    void shouldThrowExceptionWhenCNPJAlreadyExists() {
        UpsertCustomerRequest request = new UpsertCustomerRequest(
                "Empresa XYZ",
                "CNPJ",
                "11.222.333/0001-81",
                "(11) 3456-7890",
                "empresa@example.com",
                "Av. B, 456"
        );

        when(customerRepository.existsByDocumentNumber("11222333000181")).thenReturn(true);

        assertThatThrownBy(() -> customerUseCase.create(request))
                .isInstanceOf(DuplicateDocumentException.class)
                .hasMessageContaining("já existe");

        verify(customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve buscar cliente por ID com sucesso")
    void shouldFindCustomerByIdSuccessfully() {
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer(
                customerId,
                "João Silva",
                DocumentType.CPF,
                "12345678909",
                "11987654321",
                "joao@example.com",
                "Rua A, 123",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        CustomerResponse expectedResponse = new CustomerResponse(
                customerId,
                "João Silva",
                "CPF",
                "123.456.789-09",
                "(11) 9 8765-4321",
                "joao@example.com",
                "Rua A, 123",
                customer.getCreatedAt(),
                customer.getUpdatedAt()
        );

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(customerMapper.toResponse(customer)).thenReturn(expectedResponse);

        CustomerResponse response = customerUseCase.findById(customerId);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(customerId);
        assertThat(response.getName()).isEqualTo("João Silva");
        verify(customerRepository).findById(customerId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar cliente inexistente")
    void shouldThrowExceptionWhenCustomerNotFound() {
        UUID customerId = UUID.randomUUID();
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerUseCase.findById(customerId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("não encontrado");
    }

    @Test
    @DisplayName("Deve listar todos os clientes com sucesso")
    void shouldFindAllCustomersSuccessfully() {
        List<Customer> customers = new ArrayList<>();
        customers.add(new Customer(
                UUID.randomUUID(),
                "João Silva",
                DocumentType.CPF,
                "12345678909",
                "11987654321",
                "joao@example.com",
                "Rua A, 123",
                LocalDateTime.now(),
                LocalDateTime.now()
        ));
        customers.add(new Customer(
                UUID.randomUUID(),
                "Empresa XYZ",
                DocumentType.CNPJ,
                "11222333000181",
                "1134567890",
                "empresa@example.com",
                "Av. B, 456",
                LocalDateTime.now(),
                LocalDateTime.now()
        ));

        List<CustomerResponse> responses = new ArrayList<>();
        for (Customer customer : customers) {
            responses.add(new CustomerResponse(
                    customer.getId(),
                    customer.getName(),
                    customer.getDocumentType().getValue(),
                    customer.getDocumentNumber(),
                    customer.getTelephone(),
                    customer.getEmail(),
                    customer.getAddress(),
                    customer.getCreatedAt(),
                    customer.getUpdatedAt()
            ));
        }

        when(customerRepository.findAll()).thenReturn(customers);
        for (int i = 0; i < customers.size(); i++) {
            when(customerMapper.toResponse(customers.get(i))).thenReturn(responses.get(i));
        }

        List<CustomerResponse> result = customerUseCase.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(CustomerResponse::getName).containsExactly("João Silva", "Empresa XYZ");
        verify(customerRepository).findAll();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há clientes")
    void shouldReturnEmptyListWhenNoCustomersExist() {
        when(customerRepository.findAll()).thenReturn(new ArrayList<>());

        List<CustomerResponse> result = customerUseCase.findAll();

        assertThat(result).isEmpty();
        verify(customerRepository).findAll();
    }

    @Test
    @DisplayName("Deve atualizar cliente com sucesso (dados diferentes do documento)")
    void shouldUpdateCustomerSuccessfully() {
        UUID customerId = UUID.randomUUID();

        Customer existingCustomer = new Customer(
                customerId,
                "João Silva",
                DocumentType.CPF,
                "12345678909",
                "5511987654321",
                "joao@example.com",
                "Rua A, 123",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        UpsertCustomerRequest updateRequest = new UpsertCustomerRequest(
                "João Silva Santos",
                "CPF",
                "123.456.789-09",
                "55 (11) 99999-9999",
                "joao.santos@example.com",
                "Rua A, 456"
        );

        Customer updatedCustomer = new Customer(
                customerId,
                "João Silva Santos",
                DocumentType.CPF,
                "12345678909",
                "5511999999999",
                "joao.santos@example.com",
                "Rua A, 456",
                existingCustomer.getCreatedAt(),
                LocalDateTime.now()
        );

        CustomerResponse expectedResponse = new CustomerResponse(
                customerId,
                "João Silva Santos",
                "CPF",
                "123.456.789-09",
                "55 (11) 9 9999-9999",
                "joao.santos@example.com",
                "Rua A, 456",
                existingCustomer.getCreatedAt(),
                updatedCustomer.getUpdatedAt()
        );

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.existsByDocumentNumberAndIdNot("12345678909", customerId)).thenReturn(false);
        when(customerMapper.toDomainEntity(updateRequest, existingCustomer)).thenReturn(updatedCustomer);
        when(customerRepository.save(updatedCustomer)).thenReturn(updatedCustomer);
        when(customerMapper.toResponse(updatedCustomer)).thenReturn(expectedResponse);

        CustomerResponse response = customerUseCase.update(customerId, updateRequest);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(customerId);
        assertThat(response.getName()).isEqualTo("João Silva Santos");
        assertThat(response.getAddress()).isEqualTo("Rua A, 456");

        verify(customerRepository).findById(customerId);
        verify(customerRepository).existsByDocumentNumberAndIdNot("12345678909", customerId);
        verify(customerRepository).save(updatedCustomer);
        verify(customerMapper).toDomainEntity(updateRequest, existingCustomer);
        verify(customerMapper).toResponse(updatedCustomer);
    }

    @Test
    @DisplayName("Não deve permitir atualizar cliente com documento diferente do existente")
    void shouldNotAllowUpdateWithDifferentDocument() {
        UUID customerId = UUID.randomUUID();
        Customer existingCustomer = new Customer(
                customerId,
                "João Silva",
                DocumentType.CPF,
                "12345678909",
                "5511987654321",
                "joao@example.com",
                "Rua A, 123",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        UpsertCustomerRequest updateRequest = new UpsertCustomerRequest(
                "João Silva",
                "CPF",
                "987.654.321-00",
                "55 (11) 98765-4321",
                "joao@example.com",
                "Rua A, 123"
        );

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));

        assertThatThrownBy(() -> customerUseCase.update(customerId, updateRequest))
                .isInstanceOf(DuplicateDocumentException.class)
                .hasMessageContaining("Não é permitido alterar o documento do cliente");

        verify(customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar cliente inexistente")
    void shouldThrowExceptionWhenUpdatingNonexistentCustomer() {
        UUID customerId = UUID.randomUUID();
        UpsertCustomerRequest updateRequest = new UpsertCustomerRequest(
                "João Silva",
                "CPF",
                "123.456.789-09",
                "(11) 98765-4321",
                "joao@example.com",
                "Rua A, 123"
        );

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerUseCase.update(customerId, updateRequest))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("não encontrado");

        verify(customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve deletar cliente com sucesso")
    void shouldDeleteCustomerSuccessfully() {
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer(
                customerId,
                "João Silva",
                DocumentType.CPF,
                "12345678909",
                "11987654321",
                "joao@example.com",
                "Rua A, 123",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        doNothing().when(customerRepository).deleteById(customerId);

        customerUseCase.delete(customerId);

        verify(customerRepository).findById(customerId);
        verify(customerRepository).deleteById(customerId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar cliente inexistente")
    void shouldThrowExceptionWhenDeletingNonexistentCustomer() {
        UUID customerId = UUID.randomUUID();
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerUseCase.delete(customerId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("não encontrado");

        verify(customerRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Deve retornar documento formatado corretamente (CPF) ao buscar cliente")
    void shouldReturnFormattedCPFWhenFindingCustomer() {
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer(
                customerId,
                "João Silva",
                DocumentType.CPF,
                "12345678909",
                "11987654321",
                "joao@example.com",
                "Rua A, 123",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        CustomerResponse expectedResponse = new CustomerResponse(
                customerId,
                "João Silva",
                "CPF",
                "123.456.789-09",
                "(11) 9 8765-4321",
                "joao@example.com",
                "Rua A, 123",
                customer.getCreatedAt(),
                customer.getUpdatedAt()
        );

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(customerMapper.toResponse(customer)).thenReturn(expectedResponse);

        CustomerResponse response = customerUseCase.findById(customerId);

        assertThat(response.getDocumentNumber()).isEqualTo("123.456.789-09");
        assertThat(response.getDocumentNumber()).contains(".", "-");
    }

    @Test
    @DisplayName("Deve retornar documento formatado corretamente (CNPJ) ao buscar cliente")
    void shouldReturnFormattedCNPJWhenFindingCustomer() {
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer(
                customerId,
                "Empresa XYZ",
                DocumentType.CNPJ,
                "11222333000181",
                "1134567890",
                "empresa@example.com",
                "Av. B, 456",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        CustomerResponse expectedResponse = new CustomerResponse(
                customerId,
                "Empresa XYZ",
                "CNPJ",
                "11.222.333/0001-81",
                "(11) 3456-7890",
                "empresa@example.com",
                "Av. B, 456",
                customer.getCreatedAt(),
                customer.getUpdatedAt()
        );

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(customerMapper.toResponse(customer)).thenReturn(expectedResponse);

        CustomerResponse response = customerUseCase.findById(customerId);

        assertThat(response.getDocumentNumber()).isEqualTo("11.222.333/0001-81");
        assertThat(response.getDocumentNumber()).contains(".", "/", "-");
    }

    @Test
    @DisplayName("Deve retornar telefone formatado corretamente ao buscar cliente")
    void shouldReturnFormattedTelephoneWhenFindingCustomer() {
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer(
                customerId,
                "João Silva",
                DocumentType.CPF,
                "12345678909",
                "11987654321",
                "joao@example.com",
                "Rua A, 123",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        CustomerResponse expectedResponse = new CustomerResponse(
                customerId,
                "João Silva",
                "CPF",
                "123.456.789-09",
                "(11) 9 8765-4321",
                "joao@example.com",
                "Rua A, 123",
                customer.getCreatedAt(),
                customer.getUpdatedAt()
        );

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(customerMapper.toResponse(customer)).thenReturn(expectedResponse);

        CustomerResponse response = customerUseCase.findById(customerId);

        assertThat(response.getTelephone()).isEqualTo("(11) 9 8765-4321");
        assertThat(response.getTelephone()).contains("(", ")", "-", " ");
    }
}

