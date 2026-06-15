package com.fiap.mechanical_hub.infrastructure.http.controllers;

import com.fiap.mechanical_hub.application.command.customer.CreateCustomerCommand;
import com.fiap.mechanical_hub.application.command.customer.UpdateCustomerCommand;
import com.fiap.mechanical_hub.application.dto.customer.CustomerResponse;
import com.fiap.mechanical_hub.application.dto.customer.InsertCustomerRequest;
import com.fiap.mechanical_hub.application.dto.customer.UpdateCustomerRequest;
import com.fiap.mechanical_hub.application.mappers.CustomerMapper;
import com.fiap.mechanical_hub.application.usecases.customer.CreateCustomerUseCase;
import com.fiap.mechanical_hub.application.usecases.customer.DeleteCustomerUseCase;
import com.fiap.mechanical_hub.application.usecases.customer.FindAllCustomersUseCase;
import com.fiap.mechanical_hub.application.usecases.customer.FindCustomerByIdUseCase;
import com.fiap.mechanical_hub.application.usecases.customer.UpdateCustomerUseCase;
import com.fiap.mechanical_hub.mocks.domain.entities.CustomerMock;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CustomerControllerTest {

    private static final UUID CUSTOMER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    private final CustomerMapper mapper = mock(CustomerMapper.class);
    private final CreateCustomerUseCase createCustomerUseCase = mock(CreateCustomerUseCase.class);
    private final FindCustomerByIdUseCase findCustomerByIdUseCase = mock(FindCustomerByIdUseCase.class);
    private final FindAllCustomersUseCase findAllCustomersUseCase = mock(FindAllCustomersUseCase.class);
    private final UpdateCustomerUseCase updateCustomerUseCase = mock(UpdateCustomerUseCase.class);
    private final DeleteCustomerUseCase deleteCustomerUseCase = mock(DeleteCustomerUseCase.class);

    private final CustomerController controller = new CustomerController(
            mapper, createCustomerUseCase, findCustomerByIdUseCase,
            findAllCustomersUseCase, updateCustomerUseCase, deleteCustomerUseCase
    );

    @Test
    void shouldReturnCreated_whenCustomerIsCreated() {
        InsertCustomerRequest request = new InsertCustomerRequest("João", "CPF", "52998224725", "5511987654321", "joao@email.com", "Rua A");
        when(mapper.toCommand(request)).thenReturn(mock(CreateCustomerCommand.class));
        when(createCustomerUseCase.execute(any())).thenReturn(CustomerMock.withDefaultValues());

        ResponseEntity<CustomerResponse> response = controller.create(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void shouldDelegateToCreateUseCase_whenCreatingCustomer() {
        InsertCustomerRequest request = new InsertCustomerRequest("João", "CPF", "52998224725", "5511987654321", "joao@email.com", "Rua A");
        when(mapper.toCommand(request)).thenReturn(mock(CreateCustomerCommand.class));
        when(createCustomerUseCase.execute(any())).thenReturn(CustomerMock.withDefaultValues());

        controller.create(request);

        verify(createCustomerUseCase).execute(any());
    }

    @Test
    void shouldReturnOk_whenFindingAllCustomers() {
        CustomerResponse customerResponse = new CustomerResponse(CUSTOMER_ID, "João", "529.982.247-25", "joao@email.com", "+55 (11) 98765-4321");
        when(findAllCustomersUseCase.execute()).thenReturn(List.of(customerResponse));

        ResponseEntity<List<CustomerResponse>> response = controller.findAll();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void shouldReturnOk_whenFindingCustomerById() {
        CustomerResponse customerResponse = new CustomerResponse(CUSTOMER_ID, "João", "529.982.247-25", "joao@email.com", "+55 (11) 98765-4321");
        when(findCustomerByIdUseCase.execute(CUSTOMER_ID)).thenReturn(customerResponse);

        ResponseEntity<CustomerResponse> response = controller.findById(CUSTOMER_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void shouldReturnOk_whenUpdatingCustomer() {
        UpdateCustomerRequest request = new UpdateCustomerRequest("João Updated", "5511987654321", "joao@email.com", "Rua B");
        when(mapper.toUpdateCommand(any(), any())).thenReturn(mock(UpdateCustomerCommand.class));
        when(updateCustomerUseCase.execute(any())).thenReturn(CustomerMock.withDefaultValues());

        ResponseEntity<CustomerResponse> response = controller.update(CUSTOMER_ID, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void shouldReturnNoContent_whenDeletingCustomer() {
        ResponseEntity<Void> response = controller.delete(CUSTOMER_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(deleteCustomerUseCase).execute(CUSTOMER_ID);
    }
}
