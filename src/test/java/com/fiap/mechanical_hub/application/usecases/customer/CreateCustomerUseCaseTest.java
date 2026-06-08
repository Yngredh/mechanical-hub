package com.fiap.mechanical_hub.application.usecases.customer;

import com.fiap.mechanical_hub.application.command.customer.CreateCustomerCommand;
import com.fiap.mechanical_hub.domain.entities.Customer;
import com.fiap.mechanical_hub.domain.exceptions.DuplicatedDocumentException;
import com.fiap.mechanical_hub.domain.repositories.CustomerRepository;
import com.fiap.mechanical_hub.domain.service.CustomerDomainService;
import com.fiap.mechanical_hub.domain.valueobjects.Document;
import com.fiap.mechanical_hub.domain.enums.DocumentTypeEnum;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateCustomerUseCaseTest {

    private final CustomerRepository customerRepository = mock(CustomerRepository.class);
    private final CustomerDomainService customerDomainService = mock(CustomerDomainService.class);
    private final CreateCustomerUseCase useCase = new CreateCustomerUseCase(customerRepository, customerDomainService);

    @Test
    void shouldCreateCustomer_whenCommandIsValid() {
        CreateCustomerCommand command = new CreateCustomerCommand(
                "João Silva",
                "CPF",
                "52998224725",
                "5511987654321",
                "joao@email.com",
                "Rua A, 123"
        );
        Customer savedCustomer = Customer.create(
                command.name(),
                new Document(DocumentTypeEnum.CPF, command.documentNumber()),
                command.telephone(),
                command.email(),
                command.address()
        );
        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

        Customer result = useCase.execute(command);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("João Silva");
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void shouldThrowException_whenDocumentAlreadyExists() {
        CreateCustomerCommand command = new CreateCustomerCommand(
                "João Silva",
                "CPF",
                "52998224725",
                "5511987654321",
                "joao@email.com",
                "Rua A, 123"
        );
        doThrow(new DuplicatedDocumentException("Documento duplicado"))
                .when(customerDomainService).validateUniqueDocument(any(Document.class));

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(DuplicatedDocumentException.class)
                .hasMessageContaining("duplicado");
    }

    @Test
    void shouldCreateCustomerWithCNPJ_whenTypeIsCNPJ() {
        CreateCustomerCommand command = new CreateCustomerCommand(
                "Empresa LTDA",
                "CNPJ",
                "11222333000181",
                "5511333344440",
                "empresa@email.com",
                "Avenida B, 456"
        );
        Customer savedCustomer = Customer.create(
                command.name(),
                new Document(DocumentTypeEnum.CNPJ, command.documentNumber()),
                command.telephone(),
                command.email(),
                command.address()
        );
        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

        Customer result = useCase.execute(command);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Empresa LTDA");
    }

    @Test
    void shouldCallValidateUniqueDocument_beforeSaving() {
        CreateCustomerCommand command = new CreateCustomerCommand(
                "João Silva",
                "CPF",
                "52998224725",
                "5511987654321",
                "joao@email.com",
                "Rua A, 123"
        );
        Customer savedCustomer = Customer.create(
                command.name(),
                new Document(DocumentTypeEnum.CPF, command.documentNumber()),
                command.telephone(),
                command.email(),
                command.address()
        );
        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

        useCase.execute(command);

        verify(customerDomainService).validateUniqueDocument(any(Document.class));
    }

    @Test
    void shouldSaveCustomerToRepository_afterCreation() {
        CreateCustomerCommand command = new CreateCustomerCommand(
                "Maria Santos",
                "CPF",
                "52998224725",
                "5511912345678",
                "maria@email.com",
                "Rua C, 789"
        );
        Customer savedCustomer = Customer.create(
                command.name(),
                new Document(DocumentTypeEnum.CPF, command.documentNumber()),
                command.telephone(),
                command.email(),
                command.address()
        );
        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

        Customer result = useCase.execute(command);

        assertThat(result.getEmail()).isEqualTo("maria@email.com");
        verify(customerRepository).save(any(Customer.class));
    }
}

