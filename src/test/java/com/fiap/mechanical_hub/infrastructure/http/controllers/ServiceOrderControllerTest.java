package com.fiap.mechanical_hub.infrastructure.http.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.mechanical_hub.application.dto.customer.CustomerResponse;
import com.fiap.mechanical_hub.application.dto.serviceorder.*;
import com.fiap.mechanical_hub.application.dto.vehicle.VehicleResponse;
import com.fiap.mechanical_hub.application.usecases.AuthorizationUseCase;
import com.fiap.mechanical_hub.application.usecases.ServiceOrderUseCase;
import com.fiap.mechanical_hub.domain.entities.Profile;
import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.domain.entities.User;
import com.fiap.mechanical_hub.domain.enums.OrderStatusEnum;
import com.fiap.mechanical_hub.domain.enums.ProfileEnum;
import com.fiap.mechanical_hub.domain.exceptions.BusinessRuleException;
import com.fiap.mechanical_hub.domain.exceptions.InvalidOrderTransitionException;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.infrastructure.database.repositories.UserJpaRepository;
import com.fiap.mechanical_hub.infrastructure.security.TokenService;
import com.fiap.mechanical_hub.infrastructure.security.UserSecurityAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ServiceOrderController.class)
@DisplayName("ServiceOrderController")
class ServiceOrderControllerTest {

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
    private UUID customerId;
    private UUID vehicleId;
    private UUID createdByUserId;
    private UUID serviceId;
    private CreateServiceOrderRequest createRequest;
    private ServiceOrderResponse serviceOrderResponse;
    private ServiceOrderSummaryResponse summaryResponse;
    private ServiceOrderDetailResponse detailResponse;
    private AddServicesToOrderRequest addServicesRequest;
    private UpdateStatusRequest updateStatusRequest;
    private UpdateStatusRequest updateTaskStatusRequest;
    private UserSecurityAdapter mechanicalPrincipal;

    @BeforeEach
    void setUp() {
        serviceOrderId = UUID.randomUUID();
        customerId = UUID.randomUUID();
        vehicleId = UUID.randomUUID();
        createdByUserId = UUID.randomUUID();
        serviceId = UUID.randomUUID();

        createRequest = new CreateServiceOrderRequest(
                new CustomerData(
                        "Ana Souza",
                        "CPF",
                        "529.982.247-25",
                        "(11) 91234-5678",
                        "ana@email.com",
                        "Rua A, 123"
                ),
                new VehicleData(
                        "ABC-1D23",
                        "Toyota",
                        "Corolla",
                        2022,
                        "Prata"
                ),
                "Troca de oleo"
        );

        serviceOrderResponse = new ServiceOrderResponse(
                serviceOrderId,
                vehicleId,
                customerId,
                OrderStatusEnum.RECEBIDO.getDisplayName(),
                createdByUserId,
                null,
                "OS-202605-0001",
                "Troca de oleo",
                BigDecimal.ZERO,
                false,
                null,
                null,
                null,
                null,
                LocalDateTime.now(),
                LocalDateTime.now(),
                List.of()
        );

        summaryResponse = new ServiceOrderSummaryResponse(
                serviceOrderId,
                "OS-202605-0001",
                OrderStatusEnum.RECEBIDO.getDisplayName(),
                false,
                LocalDateTime.now()
        );

        detailResponse = new ServiceOrderDetailResponse(
                serviceOrderId,
                "OS-202605-0001",
                new CustomerResponse(
                        customerId,
                        "Ana Souza",
                        "CPF",
                        "52998224725",
                        "(11) 91234-5678",
                        "ana@email.com",
                        "Rua A, 123",
                        LocalDateTime.now(),
                        LocalDateTime.now()
                ),
                new VehicleResponse(
                        vehicleId,
                        customerId,
                        "ABC-1D23",
                        "Toyota",
                        "Corolla",
                        2022,
                        "Prata",
                        LocalDateTime.now(),
                        LocalDateTime.now()
                ),
                OrderStatusEnum.RECEBIDO.getDisplayName(),
                "Troca de oleo",
                BigDecimal.ZERO,
                false,
                List.of(),
                List.of(),
                LocalDateTime.now()
        );

        addServicesRequest = new AddServicesToOrderRequest(List.of(serviceId));
        updateStatusRequest = new UpdateStatusRequest(OrderStatusEnum.EM_DIAGNOSTICO.name(), null);
        updateTaskStatusRequest = new UpdateStatusRequest("INICIADO", null);

        Profile mechanicalProfile = Profile.create(ProfileEnum.MECHANICAL);
        User mechanicalUser = User.build(
                createdByUserId,
                "Mecanico",
                "mecanico@email.com",
                "hash",
                mechanicalProfile
        );
        mechanicalPrincipal = new UserSecurityAdapter(mechanicalUser);
    }

    @Nested
    @DisplayName("POST /service-orders")
    class Create {

        @Test
        @DisplayName("Should create service order and return 201 when request data is valid")
        void shouldReturn201WhenDataIsValid() throws Exception {
            when(serviceOrderUseCase.create(createRequest, createdByUserId))
                    .thenReturn(serviceOrderResponse);

            mockMvc.perform(post("/service-orders")
                            .with(csrf())
                            .with(user(mechanicalPrincipal))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(serviceOrderId.toString()))
                    .andExpect(jsonPath("$.orderNumber").value("OS-202605-0001"))
                    .andExpect(jsonPath("$.status").value(OrderStatusEnum.RECEBIDO.getDisplayName()));

            verify(serviceOrderUseCase).create((createRequest), (createdByUserId));
        }

        @Test
        @DisplayName("Should return 422 when business rule is violated")
        void shouldReturn422WhenBusinessRuleIsViolated() throws Exception {
            when(serviceOrderUseCase.create((createRequest), (createdByUserId)))
                    .thenThrow(new BusinessRuleException("A descrição da solicitação não pode ultrapassar 255 caracteres"));

            mockMvc.perform(post("/service-orders")
                            .with(csrf())
                            .with(user(mechanicalPrincipal))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.error").value("Violação de Regra de Negócio"));
        }

        @Test
        @DisplayName("Should return 401 when user is not authenticated")
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            mockMvc.perform(post("/service-orders")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(serviceOrderUseCase);
        }
    }

    @Nested
    @DisplayName("PATCH /service-orders/{id}/status")
    class UpdateStatus {

        @Test
        @DisplayName("Should update order status and return 200 when request is valid")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn200WhenStatusIsUpdated() throws Exception {
            ServiceOrder updatedOrder = new ServiceOrder();
            updatedOrder.setId(serviceOrderId);
            updatedOrder.setStatus(OrderStatusEnum.EM_DIAGNOSTICO);
            updatedOrder.setOrderNumber("OS-202605-0001");

            when(serviceOrderUseCase.updateOrderStatus((serviceOrderId), (OrderStatusEnum.EM_DIAGNOSTICO)))
                    .thenReturn(updatedOrder);

            mockMvc.perform(patch("/service-orders/{id}/status", serviceOrderId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateStatusRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(serviceOrderId.toString()))
                    .andExpect(jsonPath("$.status").value(OrderStatusEnum.EM_DIAGNOSTICO.name()));

            verify(serviceOrderUseCase).updateOrderStatus((serviceOrderId), (OrderStatusEnum.EM_DIAGNOSTICO));
        }

        @Test
        @DisplayName("Should return 400 when status transition is invalid")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn400WhenInvalidStatusTransition() throws Exception {
            when(serviceOrderUseCase.updateOrderStatus((serviceOrderId), (OrderStatusEnum.EM_DIAGNOSTICO)))
                    .thenThrow(new InvalidOrderTransitionException("Invalid transition"));

            mockMvc.perform(patch("/service-orders/{id}/status", serviceOrderId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateStatusRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Bad Request"));
        }

        @Test
        @DisplayName("Should return 400 when status is not recognized")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn400WhenStatusIsInvalid() throws Exception {
            UpdateStatusRequest invalidRequest = new UpdateStatusRequest("INVALID_STATUS", null);

            mockMvc.perform(patch("/service-orders/{id}/status", serviceOrderId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Bad Request"));

            verifyNoInteractions(serviceOrderUseCase);
        }

        @Test
        @DisplayName("Should return 401 when user is not authenticated")
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            mockMvc.perform(patch("/service-orders/{id}/status", serviceOrderId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateStatusRequest)))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(serviceOrderUseCase);
        }
    }

    @Nested
    @DisplayName("GET /service-orders")
    class FindAll {

        @Test
        @DisplayName("Should return 200 with service order list")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn200WithList() throws Exception {
            when(serviceOrderUseCase.findAll()).thenReturn(List.of(summaryResponse));

            mockMvc.perform(get("/service-orders"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].id").value(serviceOrderId.toString()))
                    .andExpect(jsonPath("$[0].orderNumber").value("OS-202605-0001"));

            verify(serviceOrderUseCase).findAll();
        }

        @Test
        @DisplayName("Should return 200 with empty list when no orders exist")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn200WithEmptyListWhenNoOrdersExist() throws Exception {
            when(serviceOrderUseCase.findAll()).thenReturn(List.of());

            mockMvc.perform(get("/service-orders"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @DisplayName("Should return 401 when user is not authenticated")
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            mockMvc.perform(get("/service-orders"))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(serviceOrderUseCase);
        }
    }

    @Nested
    @DisplayName("GET /service-orders/{id}")
    class FindById {

        @Test
        @DisplayName("Should return 200 with order details when ID exists")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn200WhenOrderExists() throws Exception {
            when(serviceOrderUseCase.findById(serviceOrderId)).thenReturn(detailResponse);

            mockMvc.perform(get("/service-orders/{id}", serviceOrderId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(serviceOrderId.toString()))
                    .andExpect(jsonPath("$.orderNumber").value("OS-202605-0001"))
                    .andExpect(jsonPath("$.customer.id").value(customerId.toString()));

            verify(serviceOrderUseCase).findById(serviceOrderId);
        }

        @Test
        @DisplayName("Should return 404 when order is not found")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn404WhenOrderDoesNotExist() throws Exception {
            when(serviceOrderUseCase.findById(serviceOrderId))
                    .thenThrow(new NotFoundException("Ordem de serviço não encontrada"));

            mockMvc.perform(get("/service-orders/{id}", serviceOrderId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("Not Found"))
                    .andExpect(jsonPath("$.status").value(404));
        }

        @Test
        @DisplayName("Should return 401 when user is not authenticated")
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            mockMvc.perform(get("/service-orders/{id}", serviceOrderId))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(serviceOrderUseCase);
        }
    }

    @Nested
    @DisplayName("POST /service-orders/{id}/services")
    class AddServices {

        @Test
        @DisplayName("Should add services and return 204 when request is valid")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn204WhenServicesAreAdded() throws Exception {
            doNothing().when(serviceOrderUseCase).addServices((serviceOrderId), (addServicesRequest));

            mockMvc.perform(post("/service-orders/{id}/services", serviceOrderId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(addServicesRequest)))
                    .andExpect(status().isNoContent());

            verify(serviceOrderUseCase).addServices((serviceOrderId), (addServicesRequest));
        }

        @Test
        @DisplayName("Should return 404 when order is not found")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn404WhenOrderDoesNotExist() throws Exception {
            doThrow(new NotFoundException("Ordem de serviço não encontrada"))
                    .when(serviceOrderUseCase).addServices((serviceOrderId), (addServicesRequest));

            mockMvc.perform(post("/service-orders/{id}/services", serviceOrderId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(addServicesRequest)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("Not Found"))
                    .andExpect(jsonPath("$.status").value(404));
        }

        @Test
        @DisplayName("Should return 401 when user is not authenticated")
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            mockMvc.perform(post("/service-orders/{id}/services", serviceOrderId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(addServicesRequest)))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(serviceOrderUseCase);
        }
    }

    @Nested
    @DisplayName("PATCH /service-orders/{id}/services/{taskId}/status")
    class UpdateTaskStatus {

        @Test
        @DisplayName("Should update task status and return 204 when request is valid")
        @WithMockUser(roles = "MECHANICAL")
        void shouldReturn204WhenTaskStatusIsUpdated() throws Exception {
            doNothing().when(serviceOrderUseCase)
                    .updateTaskStatus(eq(serviceOrderId), eq(serviceId), any());

            mockMvc.perform(patch("/service-orders/{id}/services/{taskId}/status", serviceOrderId, serviceId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateTaskStatusRequest)))
                    .andExpect(status().isNoContent());

            verify(serviceOrderUseCase).updateTaskStatus(eq(serviceOrderId), eq(serviceId), any());
        }

        @Test
        @DisplayName("Should return 404 when order or task is not found")
        @WithMockUser(roles = "MECHANICAL")
        void shouldReturn404WhenTaskNotFound() throws Exception {
            doThrow(new NotFoundException("Ordem de serviço não encontrada"))
                    .when(serviceOrderUseCase).updateTaskStatus(eq(serviceOrderId), eq(serviceId), any());

            mockMvc.perform(patch("/service-orders/{id}/services/{taskId}/status", serviceOrderId, serviceId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateTaskStatusRequest)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("Not Found"));
        }

        @Test
        @DisplayName("Should return 400 when task status is invalid")
        @WithMockUser(roles = "MECHANICAL")
        void shouldReturn400WhenTaskStatusIsInvalid() throws Exception {
            UpdateStatusRequest invalidRequest = new UpdateStatusRequest("INVALID_STATUS", null);

            mockMvc.perform(patch("/service-orders/{id}/services/{taskId}/status", serviceOrderId, serviceId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Bad Request"));

            verifyNoInteractions(serviceOrderUseCase);
        }

        @Test
        @DisplayName("Should return 401 when user is not authenticated")
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            mockMvc.perform(patch("/service-orders/{id}/services/{taskId}/status", serviceOrderId, serviceId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateTaskStatusRequest)))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(serviceOrderUseCase);
        }
    }
}

