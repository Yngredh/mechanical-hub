package com.fiap.mechanical_hub.application.mappers;

import com.fiap.mechanical_hub.application.dto.customer.UpsertCustomerRequest;
import com.fiap.mechanical_hub.application.dto.customer.CustomerResponse;
import com.fiap.mechanical_hub.domain.entities.Customer;
import com.fiap.mechanical_hub.domain.enums.DocumentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Testes do Mapper de Clientes")
class CustomerMapperTest {

    private final CustomerMapper customerMapper = new CustomerMapper();

    @Test
    @DisplayName("Deve mapear UpsertCustomerRequest para Customer")
    void shouldMapRequestToDomainEntity() {
        UpsertCustomerRequest request = new UpsertCustomerRequest(
                "Anabela Almeida",
                "CPF",
                "111.444.777-35",
                "+55 (11) 98765-4321",
                "joao@example.com",
                "Rua A, 123"
        );

        Customer customer = customerMapper.toDomainEntity(request);

        assertThat(customer).isNotNull();
        assertThat(customer.getId()).isNotNull();
        assertThat(customer.getName()).isEqualTo("Anabela Almeida");
        assertThat(customer.getDocumentType()).isEqualTo(DocumentType.CPF);
        assertThat(customer.getDocumentNumber()).isEqualTo("11144477735");
        assertThat(customer.getTelephone()).isEqualTo("5511987654321");
        assertThat(customer.getEmail()).isEqualTo("joao@example.com");
        assertThat(customer.getAddress()).isEqualTo("Rua A, 123");
        assertThat(customer.getCreatedAt()).isNotNull();
        assertThat(customer.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Deve mapear UpsertCustomerRequest para um Customer existente")
    void shouldMapRequestToExistingCustomer() {
        UUID customerId = UUID.randomUUID();
        Customer existingCustomer = new Customer(
                customerId,
                "João Silva",
                DocumentType.CPF,
                "11144477735",
                "11987654321",
                "joao@example.com",
                "Rua A, 123",
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now()
        );

        UpsertCustomerRequest request = new UpsertCustomerRequest(
                "João Silva Santos",
                "CPF",
                "111.444.777-35",
                "55 (11) 99999-9999",
                "joao.santos@example.com",
                "Rua A, 456"
        );

        Customer updatedCustomer = customerMapper.toDomainEntity(request, existingCustomer);

        assertThat(updatedCustomer).isNotNull();
        assertThat(updatedCustomer.getId()).isEqualTo(customerId);
        assertThat(updatedCustomer.getName()).isEqualTo("João Silva Santos");
        assertThat(updatedCustomer.getDocumentType()).isEqualTo(DocumentType.CPF);
        assertThat(updatedCustomer.getDocumentNumber()).isEqualTo("11144477735");
        assertThat(updatedCustomer.getTelephone()).isEqualTo("5511999999999");
        assertThat(updatedCustomer.getEmail()).isEqualTo("joao.santos@example.com");
        assertThat(updatedCustomer.getAddress()).isEqualTo("Rua A, 456");
    }

    @Test
    @DisplayName("Deve mapear request com CNPJ para Customer")
    void shouldMapRequestWithCNPJToDomainEntity() {
        UpsertCustomerRequest request = new UpsertCustomerRequest(
                "Empresa XYZ",
                "CNPJ",
                "11.222.333/0001-81",
                "55 (11) 3456-7890",
                "empresa@example.com",
                "Av. B, 456"
        );

        Customer customer = customerMapper.toDomainEntity(request);

        assertThat(customer).isNotNull();
        assertThat(customer.getDocumentType()).isEqualTo(DocumentType.CNPJ);
        assertThat(customer.getDocumentNumber()).isEqualTo("11222333000181");
    }

    @Test
    @DisplayName("Deve mapear Customer para CustomerResponse com formatação de CPF")
    void shouldMapCustomerToResponseWithFormattedCPF() {
        UUID customerId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        Customer customer = new Customer(
                customerId,
                "João Silva",
                DocumentType.CPF,
                "11144477735",
                "11987654321",
                "joao@example.com",
                "Rua A, 123",
                now,
                now
        );

        CustomerResponse response = customerMapper.toResponse(customer);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(customerId);
        assertThat(response.getName()).isEqualTo("João Silva");
        assertThat(response.getDocumentType()).isEqualTo("CPF");
        assertThat(response.getDocumentNumber()).isEqualTo("111.444.777-35");
        assertThat(response.getTelephone()).isEqualTo("(11) 9 8765-4321");
        assertThat(response.getEmail()).isEqualTo("joao@example.com");
        assertThat(response.getAddress()).isEqualTo("Rua A, 123");
        assertThat(response.getCreatedAt()).isEqualTo(now);
        assertThat(response.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Deve mapear Customer para CustomerResponse com formatação de CNPJ")
    void shouldMapCustomerToResponseWithFormattedCNPJ() {
        UUID customerId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        Customer customer = new Customer(
                customerId,
                "Empresa XYZ",
                DocumentType.CNPJ,
                "11222333000181",
                "1134567890",
                "empresa@example.com",
                "Av. B, 456",
                now,
                now
        );

        CustomerResponse response = customerMapper.toResponse(customer);

        assertThat(response).isNotNull();
        assertThat(response.getDocumentType()).isEqualTo("CNPJ");
        assertThat(response.getDocumentNumber()).isEqualTo("11.222.333/0001-81");
        assertThat(response.getTelephone()).isEqualTo("(11) 3456-7890");
    }

    @Test
    @DisplayName("Deve preservar estrutura de dados no mapeamento completo (DTO -> Entity -> Response)")
    void shouldPreserveDataInCompleteMapping() {
        UpsertCustomerRequest request = new UpsertCustomerRequest(
                "João Silva",
                "CPF",
                "111.444.777-35",
                "55 (11) 98765-4321",
                "joao@example.com",
                "Rua A, 123"
        );

        Customer customer = customerMapper.toDomainEntity(request);
        CustomerResponse response = customerMapper.toResponse(customer);

        assertThat(response.getName()).isEqualTo(request.getName());
        assertThat(response.getDocumentType()).isEqualTo(request.getDocumentType());
        assertThat(response.getEmail()).isEqualTo(request.getEmail());
        assertThat(response.getAddress()).isEqualTo(request.getAddress());

        assertThat(response.getDocumentNumber()).isEqualTo("111.444.777-35");
        assertThat(response.getTelephone()).isEqualTo("+55 (11) 9 8765-4321");
    }

    @Test
    @DisplayName("Deve formatar CPF corretamente em CustomerResponse")
    void shouldFormatCPFCorrectlyInResponse() {
        Customer customer = new Customer(
                UUID.randomUUID(),
                "Cliente",
                DocumentType.CPF,
                "12345678910",
                "11987654321",
                "cliente@example.com",
                "Rua",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        CustomerResponse response = customerMapper.toResponse(customer);

        assertThat(response.getDocumentNumber()).matches("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}");
        assertThat(response.getDocumentNumber()).contains(".", "-");
    }

    @Test
    @DisplayName("Deve formatar CNPJ corretamente em CustomerResponse")
    void shouldFormatCNPJCorrectlyInResponse() {
        Customer customer = new Customer(
                UUID.randomUUID(),
                "Empresa",
                DocumentType.CNPJ,
                "11222333000181",
                "1134567890",
                "empresa@example.com",
                "Avenida",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        CustomerResponse response = customerMapper.toResponse(customer);

        assertThat(response.getDocumentNumber()).matches("\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}");
        assertThat(response.getDocumentNumber()).contains(".", "/", "-");
    }

    @Test
    @DisplayName("Deve formatar telefone celular (11 dígitos) corretamente")
    void shouldFormatMobilePhoneCorrectly() {
        Customer customer = new Customer(
                UUID.randomUUID(),
                "Cliente",
                DocumentType.CPF,
                "12345678910",
                "11987654321",
                "cliente@example.com",
                "Rua",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        CustomerResponse response = customerMapper.toResponse(customer);

        assertThat(response.getTelephone()).matches("\\(\\d{2}\\) \\d \\d{4}-\\d{4}");
        assertThat(response.getTelephone()).isEqualTo("(11) 9 8765-4321");
    }

    @Test
    @DisplayName("Deve formatar telefone fixo (10 dígitos) corretamente")
    void shouldFormatLandlinePhoneCorrectly() {
        Customer customer = new Customer(
                UUID.randomUUID(),
                "Cliente",
                DocumentType.CPF,
                "12345678910",
                "1134567890",
                "cliente@example.com",
                "Rua",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        CustomerResponse response = customerMapper.toResponse(customer);

        assertThat(response.getTelephone()).matches("\\(\\d{2}\\) \\d{4}-\\d{4}");
        assertThat(response.getTelephone()).isEqualTo("(11) 3456-7890");
    }

    @Test
    @DisplayName("Deve formatar telefone com código de país (12 dígitos) corretamente")
    void shouldFormatPhoneWithCountryCode12DigitsCorrectly() {
        Customer customer = new Customer(
                UUID.randomUUID(),
                "Cliente",
                DocumentType.CPF,
                "12345678910",
                "551134567890",
                "cliente@example.com",
                "Rua",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        CustomerResponse response = customerMapper.toResponse(customer);

        assertThat(response.getTelephone()).matches("\\+55 \\(\\d{2}\\) \\d{4}-\\d{4}");
        assertThat(response.getTelephone()).isEqualTo("+55 (11) 3456-7890");
    }

    @Test
    @DisplayName("Deve formatar telefone com código de país (13 dígitos) corretamente")
    void shouldFormatPhoneWithCountryCode13DigitsCorrectly() {
        Customer customer = new Customer(
                UUID.randomUUID(),
                "Cliente",
                DocumentType.CPF,
                "12345678910",
                "5511987654321",
                "cliente@example.com",
                "Rua",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        CustomerResponse response = customerMapper.toResponse(customer);

        assertThat(response.getTelephone()).matches("\\+55 \\(\\d{2}\\) \\d \\d{4}-\\d{4}");
        assertThat(response.getTelephone()).isEqualTo("+55 (11) 9 8765-4321");
    }

    @Test
    @DisplayName("Deve lidar corretamente com request sem formatação")
    void shouldHandleUnformattedRequest() {
        UpsertCustomerRequest request = new UpsertCustomerRequest(
                "João Silva",
                "CPF",
                "11144477735",
                "5511987654321",
                "joao@example.com",
                "Rua A, 123"
        );

        Customer customer = customerMapper.toDomainEntity(request);
        CustomerResponse response = customerMapper.toResponse(customer);

        assertThat(response.getDocumentNumber()).isEqualTo("111.444.777-35");
        assertThat(response.getTelephone()).isEqualTo("+55 (11) 9 8765-4321");
    }

    @Test
    @DisplayName("Deve gerar UUID diferente para cada novo mapeamento")
    void shouldGenerateDifferentUUIDForEachMapping() {
        UpsertCustomerRequest request1 = new UpsertCustomerRequest(
                "João Silva",
                "CPF",
                "111.444.777-35",
                "55(11) 98765-4321",
                "joao@example.com",
                "Rua A, 123"
        );

        UpsertCustomerRequest request2 = new UpsertCustomerRequest(
                "Maria Santos",
                "CPF",
                "111.444.777-35",
                "55(11) 98765-4322",
                "maria@example.com",
                "Rua B, 456"
        );

        Customer customer1 = customerMapper.toDomainEntity(request1);
        Customer customer2 = customerMapper.toDomainEntity(request2);

        assertThat(customer1.getId()).isNotEqualTo(customer2.getId());
    }

    @Test
    @DisplayName("Deve preservar ID ao atualizar customer existente")
    void shouldPreserveIDWhenUpdatingExistingCustomer() {
        UUID originalId = UUID.randomUUID();
        Customer existingCustomer = new Customer(
                originalId,
                "João Silva",
                DocumentType.CPF,
                "11144477735",
                "11987654321",
                "joao@example.com",
                "Rua A, 123",
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now()
        );

        UpsertCustomerRequest request = new UpsertCustomerRequest(
                "João Silva Santos",
                "CPF",
                "111.444.777-35",
                "55 (11) 99999-9999",
                "joao.santos@example.com",
                "Rua A, 456"
        );

        Customer updatedCustomer = customerMapper.toDomainEntity(request, existingCustomer);

        assertThat(updatedCustomer.getId()).isEqualTo(originalId);
    }
}

