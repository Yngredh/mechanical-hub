package com.fiap.mechanical_hub.application.usecases;

import com.fiap.mechanical_hub.application.dto.customer.CustomerResponse;
import com.fiap.mechanical_hub.application.dto.customer.UpsertCustomerRequest;
import com.fiap.mechanical_hub.application.mappers.CustomerMapper;
import com.fiap.mechanical_hub.application.repositories.CustomerRepository;
import com.fiap.mechanical_hub.domain.entities.Customer;
import com.fiap.mechanical_hub.domain.enums.DocumentTypeEnum;
import com.fiap.mechanical_hub.domain.exceptions.DuplicateDocumentException;
import com.fiap.mechanical_hub.domain.exceptions.InvalidDocumentException;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.shared.utils.Formatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do CustomerUseCase")
class CustomerUseCaseTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerUseCase customerUseCase;

    private UUID customerId;
    private Customer customer;
    private CustomerResponse customerResponse;
    private UpsertCustomerRequest upsertRequest;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        customer = new Customer(
                customerId,
                "João Silva",
                DocumentTypeEnum.CPF,
                "12345678901",
                "11987654321",
                "joao@example.com",
                "Rua A, 123",
                now,
                now
        );

        customerResponse = new CustomerResponse(
                customerId,
                "João Silva",
                "CPF",
                "123.456.789-01",
                "(11) 98765-4321",
                "joao@example.com",
                "Rua A, 123",
                now,
                now
        );

        upsertRequest = new UpsertCustomerRequest(
                "João Silva",
                "CPF",
                "123.456.789-01",
                "(11) 98765-4321",
                "joao@example.com",
                "Rua A, 123"
        );
    }

    @Test
    @DisplayName("Deve criar cliente com sucesso")
    void shouldCreateCustomerSuccessfully() {
        try (MockedStatic<com.fiap.mechanical_hub.shared.utils.Formatter> formatterMock = mockStatic(com.fiap.mechanical_hub.shared.utils.Formatter.class)) {
            formatterMock.when(() -> com.fiap.mechanical_hub.shared.utils.Formatter.removeFormatting("123.456.789-01")).thenReturn("12345678901");

            when(customerRepository.existsByDocumentNumber("12345678901")).thenReturn(false);
            when(customerMapper.toDomainEntity(upsertRequest)).thenReturn(customer);
            when(customerRepository.save(customer)).thenReturn(customer);
            when(customerMapper.toResponse(customer)).thenReturn(customerResponse);

            CustomerResponse result = customerUseCase.create(upsertRequest);

            assertNotNull(result);
            assertEquals("João Silva", result.getName());
            verify(customerRepository).existsByDocumentNumber("12345678901");
            verify(customerRepository).save(customer);
            verify(customerMapper).toResponse(customer);
        }
    }

    @Test
    @DisplayName("Deve lançar DuplicateDocumentException ao criar cliente com documento duplicado")
    void shouldThrowDuplicateDocumentWhenCreatingWithExistingDocument() {
        try (MockedStatic<com.fiap.mechanical_hub.shared.utils.Formatter> formatterMock = mockStatic(com.fiap.mechanical_hub.shared.utils.Formatter.class)) {
            formatterMock.when(() -> com.fiap.mechanical_hub.shared.utils.Formatter.removeFormatting("123.456.789-01")).thenReturn("12345678901");

            when(customerRepository.existsByDocumentNumber("12345678901")).thenReturn(true);

            DuplicateDocumentException exception = assertThrows(DuplicateDocumentException.class,
                    () -> customerUseCase.create(upsertRequest));
            assertEquals("Cliente com documento 123.456.789-01 já existe", exception.getMessage());
            verify(customerRepository).existsByDocumentNumber("12345678901");
            verify(customerRepository, never()).save(any());
        }
    }

    @Test
    @DisplayName("Deve encontrar cliente por ID com sucesso")
    void shouldFindCustomerByIdSuccessfully() {
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(customerMapper.toResponse(customer)).thenReturn(customerResponse);

        CustomerResponse result = customerUseCase.findById(customerId);

        assertNotNull(result);
        assertEquals(customerId, result.getId());
        verify(customerRepository).findById(customerId);
        verify(customerMapper).toResponse(customer);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando cliente não encontrado por ID")
    void shouldThrowNotFoundWhenCustomerNotFoundById() {
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> customerUseCase.findById(customerId));
        assertEquals("Cliente não encontrado para o id: " + customerId, exception.getMessage());
        verify(customerRepository).findById(customerId);
        verify(customerMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("Deve listar todos os clientes")
    void shouldFindAllCustomers() {
        List<Customer> customers = List.of(customer);
        when(customerRepository.findAll()).thenReturn(customers);
        when(customerMapper.toResponse(customer)).thenReturn(customerResponse);

        List<CustomerResponse> result = customerUseCase.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(customerId, result.get(0).getId());
        verify(customerRepository).findAll();
        verify(customerMapper).toResponse(customer);
    }

    @Test
    @DisplayName("Deve atualizar cliente com sucesso")
    void shouldUpdateCustomerSuccessfully() {
        UpsertCustomerRequest updateRequest = new UpsertCustomerRequest(
                "João Silva Atualizado",
                "CPF",
                "123.456.789-01",
                "(11) 98765-4321",
                "joao@example.com",
                "Rua B, 456"
        );

        Customer updatedCustomer = new Customer(
                customerId,
                "João Silva Atualizado",
                DocumentTypeEnum.CPF,
                "12345678901",
                "11987654321",
                "joao@example.com",
                "Rua B, 456",
                customer.getCreatedAt(),
                LocalDateTime.now()
        );

        try (MockedStatic<Formatter> formatterMock = mockStatic(Formatter.class)) {
            formatterMock.when(() -> Formatter.removeFormatting("123.456.789-01")).thenReturn("12345678901");

            when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
            when(customerRepository.existsByDocumentNumberAndIdNot("12345678901", customerId)).thenReturn(false);
            when(customerMapper.toDomainEntity(updateRequest, customer)).thenReturn(updatedCustomer);
            when(customerRepository.save(updatedCustomer)).thenReturn(updatedCustomer);
            when(customerMapper.toResponse(updatedCustomer)).thenReturn(customerResponse);

            CustomerResponse result = customerUseCase.update(customerId, updateRequest);

            assertNotNull(result);
            verify(customerRepository).findById(customerId);
            verify(customerRepository).existsByDocumentNumberAndIdNot("12345678901", customerId);
            verify(customerRepository).save(updatedCustomer);
            verify(customerMapper).toResponse(updatedCustomer);
        }
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao atualizar cliente não encontrado")
    void shouldThrowNotFoundWhenUpdatingNonExistentCustomer() {
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> customerUseCase.update(customerId, upsertRequest));
        assertEquals("Cliente não encontrado para o id: " + customerId, exception.getMessage());
        verify(customerRepository).findById(customerId);
        verify(customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar InvalidDocumentException ao tentar alterar documento")
    void shouldThrowInvalidDocumentWhenTryingToChangeDocument() {
        UpsertCustomerRequest updateRequest = new UpsertCustomerRequest(
                "João Silva",
                "CPF",
                "987.654.321-00", // Different document
                "(11) 98765-4321",
                "joao@example.com",
                "Rua A, 123"
        );

        try (MockedStatic<com.fiap.mechanical_hub.shared.utils.Formatter> formatterMock = mockStatic(com.fiap.mechanical_hub.shared.utils.Formatter.class)) {
            formatterMock.when(() -> com.fiap.mechanical_hub.shared.utils.Formatter.removeFormatting("987.654.321-00")).thenReturn("98765432100");

            when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

            InvalidDocumentException exception = assertThrows(InvalidDocumentException.class,
                    () -> customerUseCase.update(customerId, updateRequest));
            assertEquals("Não é permitido alterar o documento do cliente", exception.getMessage());
            verify(customerRepository).findById(customerId);
            verify(customerRepository, never()).save(any());
        }
    }

    @Test
    @DisplayName("Deve deletar cliente com sucesso")
    void shouldDeleteCustomerSuccessfully() {
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        doNothing().when(customerRepository).deleteById(customerId);

        assertDoesNotThrow(() -> customerUseCase.delete(customerId));

        verify(customerRepository).findById(customerId);
        verify(customerRepository).deleteById(customerId);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao deletar cliente não encontrado")
    void shouldThrowNotFoundWhenDeletingNonExistentCustomer() {
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> customerUseCase.delete(customerId));
        assertEquals("Cliente não encontrado para o id: " + customerId, exception.getMessage());
        verify(customerRepository).findById(customerId);
        verify(customerRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Deve encontrar cliente existente por documento")
    void shouldFindExistingCustomerByDocument() {
        try (MockedStatic<Formatter> formatterMock = mockStatic(com.fiap.mechanical_hub.shared.utils.Formatter.class)) {
            formatterMock.when(() -> com.fiap.mechanical_hub.shared.utils.Formatter.removeFormatting("123.456.789-01")).thenReturn("12345678901");

            when(customerRepository.findByDocumentNumber("12345678901")).thenReturn(Optional.of(customer));

            Customer result = customerUseCase.findByDocumentOrCreate(
                    "João Silva",
                    "CPF",
                    "123.456.789-01",
                    "(11) 98765-4321",
                    "joao@example.com",
                    "Rua A, 123"
            );

            assertEquals(customer, result);
            verify(customerRepository).findByDocumentNumber("12345678901");
            verify(customerRepository, never()).save(any());
        }
    }

    @Test
    @DisplayName("Deve criar novo cliente quando não encontrado por documento")
    void shouldCreateNewCustomerWhenNotFoundByDocument() {
        String documentoFormatado = "111.444.777-35";
        String documentoLimpo = "11144477735";
        String telefone = "5511987654321";

        when(customerRepository.findByDocumentNumber(documentoLimpo)).thenReturn(Optional.empty());

        when(customerRepository.save(any(Customer.class))).thenAnswer(i -> i.getArgument(0));

        Customer result = customerUseCase.findByDocumentOrCreate(
                "João Silva",
                "CPF",
                documentoFormatado,
                telefone,
                "joao@example.com",
                "Rua A, 123"
        );

        assertThat(result).isNotNull();
        assertThat(result.getDocumentNumber()).isEqualTo(documentoLimpo);
        assertThat(result.getName()).isEqualTo("João Silva");

        verify(customerRepository).findByDocumentNumber(documentoLimpo);
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    @DisplayName("Deve lançar DuplicateDocumentException quando o documento já existir para outro ID no update")
    void shouldThrowDuplicateDocumentExceptionWhenDocumentExistsForAnotherIdOnUpdate() {
        String documentNumber = "123.456.789-00";
        String cleanDocument = "12345678900";

        UpsertCustomerRequest request = new UpsertCustomerRequest();
        request.setDocumentNumber(documentNumber);

        Customer existingCustomer = new Customer(
                UUID.randomUUID(),
                "João Silva Atualizado",
                DocumentTypeEnum.CPF,
                cleanDocument,
                "11987654321",
                "joao@example.com",
                "Rua B, 456",
                customer.getCreatedAt(),
                LocalDateTime.now()
        );

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));

        when(customerRepository.existsByDocumentNumberAndIdNot(cleanDocument, customerId))
                .thenReturn(true);

        DuplicateDocumentException exception = assertThrows(DuplicateDocumentException.class, () -> {
            customerUseCase.update(customerId, request);
        });

        assertEquals(String.format("Cliente com documento %s já existe", documentNumber), exception.getMessage());
    }}
