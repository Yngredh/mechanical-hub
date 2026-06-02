package com.fiap.mechanical_hub.application.usecases.customer;

import com.fiap.mechanical_hub.mocks.domain.entities.CustomerMock;
import com.fiap.mechanical_hub.application.command.customer.FindOrCreateCustomerCommand;
import com.fiap.mechanical_hub.domain.entities.Customer;
import com.fiap.mechanical_hub.domain.repositories.CustomerRepository;
import com.fiap.mechanical_hub.domain.valueobjects.Document;
import com.fiap.mechanical_hub.domain.enums.DocumentTypeEnum;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FindOrCreateCustomerUseCaseTest {

    private final CustomerRepository customerRepository = mock(CustomerRepository.class);
    private final FindOrCreateCustomerUseCase useCase = new FindOrCreateCustomerUseCase(customerRepository);

    @Test
    void shouldReturnExistingCustomer_whenDocumentAlreadyExists() {
        FindOrCreateCustomerCommand command = new FindOrCreateCustomerCommand(
                "João Silva",
                "CPF",
                "12345678901",
                "11987654321",
                "joao@email.com",
                "Rua A, 123"
        );
        Customer existingCustomer = CustomerMock.withDefaultValues();

        when(customerRepository.findByDocumentNumber("12345678901"))
                .thenReturn(Optional.of(existingCustomer));

        Customer result = useCase.execute(command);

        assertThat(result).isEqualTo(existingCustomer);
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void shouldCreateNewCustomer_whenDocumentDoesNotExist() {
        FindOrCreateCustomerCommand command = new FindOrCreateCustomerCommand(
                "Maria Santos",
                "CPF",
                "98765432100",
                "11912345678",
                "maria@email.com",
                "Rua C, 789"
        );
        Customer newCustomer = Customer.create(
                command.name(),
                new Document(DocumentTypeEnum.CPF, command.documentNumber()),
                command.telephone(),
                command.email(),
                command.address()
        );

        when(customerRepository.findByDocumentNumber("98765432100"))
                .thenReturn(Optional.empty());
        when(customerRepository.save(any(Customer.class))).thenReturn(newCustomer);

        Customer result = useCase.execute(command);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Maria Santos");
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void shouldCallFindByDocumentNumber_withCorrectValue() {
        FindOrCreateCustomerCommand command = new FindOrCreateCustomerCommand(
                "João Silva",
                "CPF",
                "12345678901",
                "11987654321",
                "joao@email.com",
                "Rua A, 123"
        );
        Customer existingCustomer = CustomerMock.withDefaultValues();

        when(customerRepository.findByDocumentNumber("12345678901"))
                .thenReturn(Optional.of(existingCustomer));

        useCase.execute(command);

        verify(customerRepository).findByDocumentNumber("12345678901");
    }

    @Test
    void shouldCreateCustomerWithCNPJ_whenTypeIsCNPJ() {
        FindOrCreateCustomerCommand command = new FindOrCreateCustomerCommand(
                "Empresa LTDA",
                "CNPJ",
                "12345678901234",
                "1133334444",
                "empresa@email.com",
                "Avenida B, 456"
        );
        Customer newCustomer = Customer.create(
                command.name(),
                new Document(DocumentTypeEnum.CNPJ, command.documentNumber()),
                command.telephone(),
                command.email(),
                command.address()
        );

        when(customerRepository.findByDocumentNumber("12345678901234"))
                .thenReturn(Optional.empty());
        when(customerRepository.save(any(Customer.class))).thenReturn(newCustomer);

        Customer result = useCase.execute(command);

        assertThat(result).isNotNull();
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void shouldNotSaveCustomer_whenAlreadyExists() {
        FindOrCreateCustomerCommand command = new FindOrCreateCustomerCommand(
                "João Silva",
                "CPF",
                "12345678901",
                "11987654321",
                "joao@email.com",
                "Rua A, 123"
        );
        Customer existingCustomer = CustomerMock.withDefaultValues();

        when(customerRepository.findByDocumentNumber("12345678901"))
                .thenReturn(Optional.of(existingCustomer));

        useCase.execute(command);

        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void shouldReturnCreatedCustomer_whenSaved() {
        FindOrCreateCustomerCommand command = new FindOrCreateCustomerCommand(
                "Pedro Costa",
                "CPF",
                "55555555555",
                "11955555555",
                "pedro@email.com",
                "Rua D, 999"
        );
        Customer newCustomer = Customer.create(
                command.name(),
                new Document(DocumentTypeEnum.CPF, command.documentNumber()),
                command.telephone(),
                command.email(),
                command.address()
        );

        when(customerRepository.findByDocumentNumber("55555555555"))
                .thenReturn(Optional.empty());
        when(customerRepository.save(any(Customer.class))).thenReturn(newCustomer);

        Customer result = useCase.execute(command);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Pedro Costa");
    }
}

