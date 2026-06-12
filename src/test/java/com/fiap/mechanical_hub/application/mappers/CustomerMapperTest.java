package com.fiap.mechanical_hub.application.mappers;

import com.fiap.mechanical_hub.application.command.customer.CreateCustomerCommand;
import com.fiap.mechanical_hub.application.command.customer.UpdateCustomerCommand;
import com.fiap.mechanical_hub.application.dto.customer.CustomerResponse;
import com.fiap.mechanical_hub.application.dto.customer.InsertCustomerRequest;
import com.fiap.mechanical_hub.application.dto.customer.UpdateCustomerRequest;
import com.fiap.mechanical_hub.domain.entities.Customer;
import com.fiap.mechanical_hub.mocks.domain.entities.CustomerMock;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerMapperTest {

    private static final UUID CUSTOMER_ID = UUID.fromString("00000000-0000-0000-0000-000000000060");

    private final CustomerMapper mapper = new CustomerMapper();

    @Test
    void shouldMapIdAndNameAndEmail_whenConvertingToResponse() {
        Customer customer = CustomerMock.withDefaultValues();

        CustomerResponse response = CustomerMapper.toResponse(customer);

        assertThat(response.getId()).isEqualTo(customer.getId());
        assertThat(response.getName()).isEqualTo(customer.getName());
        assertThat(response.getEmail()).isEqualTo(customer.getEmail());
    }

    @Test
    void shouldFormatDocument_whenConvertingToResponse() {
        Customer customer = CustomerMock.withDefaultValues();

        CustomerResponse response = CustomerMapper.toResponse(customer);

        assertThat(response.getDocumentNumber()).isEqualTo("529.982.247-25");
    }

    @Test
    void shouldFormatTelephone_whenConvertingToResponse() {
        Customer customer = CustomerMock.withDefaultValues();

        CustomerResponse response = CustomerMapper.toResponse(customer);

        assertThat(response.getTelephone()).isEqualTo("+55 (11) 9 8765-4321");
    }

    @Test
    void shouldMapAllFields_whenConvertingInsertRequestToCreateCommand() {
        InsertCustomerRequest request = new InsertCustomerRequest(
                "João Silva", "CPF", "52998224725", "5511987654321", "joao@email.com", "Rua A, 123"
        );

        CreateCustomerCommand command = mapper.toCommand(request);

        assertThat(command.name()).isEqualTo(request.getName());
        assertThat(command.documentType()).isEqualTo(request.getDocumentType());
        assertThat(command.documentNumber()).isEqualTo(request.getDocumentNumber());
        assertThat(command.telephone()).isEqualTo(request.getTelephone());
        assertThat(command.email()).isEqualTo(request.getEmail());
        assertThat(command.address()).isEqualTo(request.getAddress());
    }

    @Test
    void shouldMapAllFields_whenConvertingUpdateRequestToUpdateCommand() {
        UpdateCustomerRequest request = new UpdateCustomerRequest(
                "João Atualizado", "5511999999999", "novo@email.com", "Rua B, 456"
        );

        UpdateCustomerCommand command = mapper.toUpdateCommand(CUSTOMER_ID, request);

        assertThat(command.id()).isEqualTo(CUSTOMER_ID);
        assertThat(command.name()).isEqualTo(request.getName());
        assertThat(command.telephone()).isEqualTo(request.getTelephone());
        assertThat(command.email()).isEqualTo(request.getEmail());
        assertThat(command.address()).isEqualTo(request.getAddress());
    }
}
