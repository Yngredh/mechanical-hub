package com.fiap.mechanical_hub.infrastructure.http.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.mechanical_hub.application.dto.serviceorder.request.ServiceOrderCustomerView;
import com.fiap.mechanical_hub.application.usecases.AuthorizationUseCase;
import com.fiap.mechanical_hub.application.usecases.ServiceOrderUseCase;
import com.fiap.mechanical_hub.domain.enums.OrderStatusEnum;
import com.fiap.mechanical_hub.domain.exceptions.InvalidOrderTransitionException;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.infrastructure.database.repositories.UserJpaRepository;
import com.fiap.mechanical_hub.infrastructure.security.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExternalController.class)
@DisplayName("ExternalController")
@AutoConfigureMockMvc(addFilters = false)
class ExternalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ServiceOrderUseCase serviceOrderUseCase;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private UserJpaRepository userJpaRepository;

    @MockBean
    private AuthorizationUseCase authorizationUseCase;

    private UUID serviceOrderId;
    private String orderNumber;
    private ServiceOrderCustomerView orderView;

    @BeforeEach
    void setUp() {
        serviceOrderId = UUID.randomUUID();
        orderNumber = "OS-202605-0001";

        orderView = new ServiceOrderCustomerView(
                orderNumber,
                "Ana Souza",
                "ABC-1D23",
                "Corolla",
                "Toyota",
                OrderStatusEnum.APROVADO,
                new BigDecimal("350.00"),
                List.of("Oil Change", "Brake Check"),
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1),
                null
        );
    }

    @Nested
    @DisplayName("POST /mechanical-hub/service-orders/{id}/approve")
    class Approve {

        @Test
        @DisplayName("Should approve service order and return 204")
        void shouldReturn204WhenApproved() throws Exception {
            doNothing().when(serviceOrderUseCase).approve(serviceOrderId);

            mockMvc.perform(post("/mechanical-hub/service-orders/{id}/approve", serviceOrderId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());

            verify(serviceOrderUseCase).approve(serviceOrderId);
        }

        @Test
        @DisplayName("Should return 404 when service order is not found")
        void shouldReturn404WhenOrderNotFound() throws Exception {
            doThrow(new NotFoundException("Service order with id " + serviceOrderId + " not found"))
                    .when(serviceOrderUseCase).approve(serviceOrderId);

            mockMvc.perform(post("/mechanical-hub/service-orders/{id}/approve", serviceOrderId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("Not Found"))
                    .andExpect(jsonPath("$.status").value(404));
        }

        @Test
        @DisplayName("Should return 400 when order status transition is invalid")
        void shouldReturn400WhenInvalidTransition() throws Exception {
            doThrow(new InvalidOrderTransitionException("Invalid transition"))
                    .when(serviceOrderUseCase).approve(serviceOrderId);

            mockMvc.perform(post("/mechanical-hub/service-orders/{id}/approve", serviceOrderId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Bad Request"));
        }
    }

    @Nested
    @DisplayName("POST /mechanical-hub/service-orders/{id}/reject")
    class Reject {

        @Test
        @DisplayName("Should reject service order and return 204")
        void shouldReturn204WhenRejected() throws Exception {
            doNothing().when(serviceOrderUseCase).reject(serviceOrderId);

            mockMvc.perform(post("/mechanical-hub/service-orders/{id}/reject", serviceOrderId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());

            verify(serviceOrderUseCase).reject(serviceOrderId);
        }

        @Test
        @DisplayName("Should return 404 when service order is not found")
        void shouldReturn404WhenOrderNotFound() throws Exception {
            doThrow(new NotFoundException("Service order with id " + serviceOrderId + " not found"))
                    .when(serviceOrderUseCase).reject(serviceOrderId);

            mockMvc.perform(post("/mechanical-hub/service-orders/{id}/reject", serviceOrderId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("Not Found"))
                    .andExpect(jsonPath("$.status").value(404));
        }

        @Test
        @DisplayName("Should return 400 when order status transition is invalid")
        void shouldReturn400WhenInvalidTransition() throws Exception {
            doThrow(new InvalidOrderTransitionException("Invalid transition"))
                    .when(serviceOrderUseCase).reject(serviceOrderId);

            mockMvc.perform(post("/mechanical-hub/service-orders/{id}/reject", serviceOrderId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Bad Request"));
        }
    }

    @Nested
    @DisplayName("GET /mechanical-hub/service-orders/{orderNumber}")
    class FindByOrderNumber {

        @Test
        @DisplayName("Should return 200 with order details")
        void shouldReturn200WithOrderDetails() throws Exception {
            when(serviceOrderUseCase.findByOrderNumber(orderNumber)).thenReturn(orderView);

            mockMvc.perform(get("/mechanical-hub/service-orders/{orderNumber}", orderNumber))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.orderNumber").value(orderNumber))
                    .andExpect(jsonPath("$.customerName").value("Ana Souza"))
                    .andExpect(jsonPath("$.vehicleLicensePlate").value("ABC-1D23"))
                    .andExpect(jsonPath("$.status").value(OrderStatusEnum.APROVADO.name()))
                    .andExpect(jsonPath("$.services").isArray())
                    .andExpect(jsonPath("$.services.length()").value(2));

            verify(serviceOrderUseCase).findByOrderNumber(orderNumber);
        }

        @Test
        @DisplayName("Should return 404 when order number is not found")
        void shouldReturn404WhenOrderNumberNotFound() throws Exception {
            when(serviceOrderUseCase.findByOrderNumber(orderNumber))
                    .thenThrow(new NotFoundException("Ordem de serviço não encontrada com número: " + orderNumber));

            mockMvc.perform(get("/mechanical-hub/service-orders/{orderNumber}", orderNumber))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("Not Found"))
                    .andExpect(jsonPath("$.status").value(404));
        }

        @Test
        @DisplayName("Should return 200 without authentication")
        void shouldReturn200WithoutAuthentication() throws Exception {
            when(serviceOrderUseCase.findByOrderNumber(orderNumber)).thenReturn(orderView);

            mockMvc.perform(get("/mechanical-hub/service-orders/{orderNumber}", orderNumber))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.orderNumber").value(orderNumber));

            verify(serviceOrderUseCase).findByOrderNumber(orderNumber);
        }
    }
}
