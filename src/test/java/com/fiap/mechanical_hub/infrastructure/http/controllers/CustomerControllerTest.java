package com.fiap.mechanical_hub.infrastructure.http.controllers;

import com.fiap.mechanical_hub.application.dto.serviceorder.ServiceOrderSummaryResponse;
import com.fiap.mechanical_hub.application.usecases.CustomerUseCase;
import com.fiap.mechanical_hub.application.usecases.ServiceOrderStatusUseCase;
import com.fiap.mechanical_hub.domain.entities.Customer;
import com.fiap.mechanical_hub.domain.entities.Vehicle;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
@DisplayName("Testes do CustomerController")
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServiceOrderStatusUseCase serviceOrderStatusUseCase;

    @MockBean
    private CustomerUseCase customerUseCase;

    @Test
    @DisplayName("Deve retornar lista de ordens de serviço do cliente com sucesso")
    void shouldReturnOrdersByCustomerIdSuccessfully() throws Exception {
        UUID customerId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID vehicleId = UUID.randomUUID();

        Customer customer = new Customer(
                customerId,
                "João Silva",
                null, // DocumentType not needed for this test
                "12345678909",
                "11987654321",
                "joao@example.com",
                "Rua A, 123",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        Vehicle vehicle = new Vehicle(
                vehicleId,
                customerId,
                "ABC-1234",
                "Fiat",
                "Uno",
                2020,
                "Branco",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        ServiceOrderSummaryResponse orderResponse = new ServiceOrderSummaryResponse(
                orderId,
                "OS-001",
                "APROVADO",
                customer,
                vehicle,
                BigDecimal.valueOf(500.00),
                LocalDateTime.now()
        );

        List<ServiceOrderSummaryResponse> orders = List.of(orderResponse);

        when(serviceOrderStatusUseCase.findByCustomerId(customerId)).thenReturn(orders);

        mockMvc.perform(get("/customers/{id}/orders", customerId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(orderId.toString()))
                .andExpect(jsonPath("$[0].orderNumber").value("OS-001"))
                .andExpect(jsonPath("$[0].status").value("APROVADO"));
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando cliente não possui ordens de serviço")
    void shouldReturnEmptyListWhenCustomerHasNoOrders() throws Exception {
        UUID customerId = UUID.randomUUID();

        when(serviceOrderStatusUseCase.findByCustomerId(customerId)).thenReturn(List.of());

        mockMvc.perform(get("/customers/{id}/orders", customerId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("Deve retornar 404 quando cliente não for encontrado")
    void shouldReturnNotFoundWhenCustomerNotFound() throws Exception {
        UUID customerId = UUID.randomUUID();

        when(serviceOrderStatusUseCase.findByCustomerId(customerId))
                .thenThrow(new NotFoundException("Customer not found"));

        mockMvc.perform(get("/customers/{id}/orders", customerId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
