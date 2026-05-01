package com.fiap.mechanical_hub.infrastructure.http.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.mechanical_hub.application.dto.service.ServiceResponse;
import com.fiap.mechanical_hub.application.dto.service.UpsertServiceRequest;
import com.fiap.mechanical_hub.application.dto.servicematerials.ServiceMaterialRequest;
import com.fiap.mechanical_hub.application.dto.servicematerials.ServiceMaterialResponse;
import com.fiap.mechanical_hub.application.usecases.AuthorizationUseCase;
import com.fiap.mechanical_hub.application.usecases.ServiceUseCase;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.infrastructure.database.repositories.UserJpaRepository;
import com.fiap.mechanical_hub.infrastructure.security.TokenService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ServiceController.class)
@DisplayName("ServiceController")
class ServiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ServiceUseCase serviceUseCase;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private UserJpaRepository userJpaRepository;

    @MockBean
    private AuthorizationUseCase authorizationUseCase;

    private UUID serviceId;
    private UUID materialId;
    private ServiceResponse serviceResponse;
    private UpsertServiceRequest upsertRequest;

    @BeforeEach
    void setUp() {
        serviceId = UUID.randomUUID();
        materialId = UUID.randomUUID();

        upsertRequest = new UpsertServiceRequest(
                "Oil Change",
                "Basic oil change",
                new BigDecimal("120.00"),
                new BigDecimal("80.00"),
                List.of(new ServiceMaterialRequest(materialId, 2))
        );

        serviceResponse = new ServiceResponse(
                serviceId,
                "Oil Change",
                "Basic oil change",
                new BigDecimal("120.00"),
                new BigDecimal("80.00"),
                new BigDecimal("200.00"),
                List.of(new ServiceMaterialResponse(
                        materialId,
                        "Oil Filter",
                        "Standard oil filter",
                        new BigDecimal("45.90"),
                        2
                )),
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Nested
    @DisplayName("POST /services")
    class Create {

        @Test
        @DisplayName("Should create service and return 201 when request data is valid")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn201WhenDataIsValid() throws Exception {
            when(serviceUseCase.create(any(UpsertServiceRequest.class))).thenReturn(serviceResponse);

            mockMvc.perform(post("/services")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(upsertRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(serviceId.toString()))
                    .andExpect(jsonPath("$.name").value("Oil Change"))
                    .andExpect(jsonPath("$.description").value("Basic oil change"))
                    .andExpect(jsonPath("$.laborCost").value(120.00))
                    .andExpect(jsonPath("$.materials").isArray())
                    .andExpect(jsonPath("$.materials.length()").value(1))
                    .andExpect(jsonPath("$.materials[0].materialId").value(materialId.toString()));

            verify(serviceUseCase).create(any(UpsertServiceRequest.class));
        }

        @Test
        @DisplayName("Should return 401 when user is not authenticated")
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            mockMvc.perform(post("/services")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(upsertRequest)))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(serviceUseCase);
        }
    }

    @Nested
    @DisplayName("GET /services")
    class FindAll {

        @Test
        @DisplayName("Should return 200 with service list")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn200WithServiceList() throws Exception {
            when(serviceUseCase.findAll()).thenReturn(List.of(serviceResponse));

            mockMvc.perform(get("/services"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].id").value(serviceId.toString()))
                    .andExpect(jsonPath("$[0].name").value("Oil Change"));

            verify(serviceUseCase).findAll();
        }

        @Test
        @DisplayName("Should return 200 with empty list when no services are registered")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn200WithEmptyListWhenNoServicesExist() throws Exception {
            when(serviceUseCase.findAll()).thenReturn(List.of());

            mockMvc.perform(get("/services"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @DisplayName("Should return 401 when user is not authenticated")
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            mockMvc.perform(get("/services"))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(serviceUseCase);
        }
    }

    @Nested
    @DisplayName("GET /services/{id}")
    class FindById {

        @Test
        @DisplayName("Should return 200 with service data when ID exists")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn200WhenServiceExists() throws Exception {
            when(serviceUseCase.findById(serviceId)).thenReturn(serviceResponse);

            mockMvc.perform(get("/services/{id}", serviceId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(serviceId.toString()))
                    .andExpect(jsonPath("$.name").value("Oil Change"))
                    .andExpect(jsonPath("$.laborCost").value(120.00));

            verify(serviceUseCase).findById(serviceId);
        }

        @Test
        @DisplayName("Should return 404 when service is not found")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn404WhenServiceDoesNotExist() throws Exception {
            when(serviceUseCase.findById(serviceId))
                    .thenThrow(new NotFoundException("Service with id " + serviceId + " not found"));

            mockMvc.perform(get("/services/{id}", serviceId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("Not Found"))
                    .andExpect(jsonPath("$.status").value(404));
        }

        @Test
        @DisplayName("Should return 401 when user is not authenticated")
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            mockMvc.perform(get("/services/{id}", serviceId))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(serviceUseCase);
        }
    }

    @Nested
    @DisplayName("PUT /services/{id}")
    class Update {

        @Test
        @DisplayName("Should update service and return 200 when request data is valid")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn200WhenDataIsValid() throws Exception {
            when(serviceUseCase.update(eq(serviceId), any(UpsertServiceRequest.class)))
                    .thenReturn(serviceResponse);

            mockMvc.perform(put("/services/{id}", serviceId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(upsertRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(serviceId.toString()))
                    .andExpect(jsonPath("$.name").value("Oil Change"));

            verify(serviceUseCase).update(eq(serviceId), any(UpsertServiceRequest.class));
        }

        @Test
        @DisplayName("Should return 404 when service is not found")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn404WhenServiceDoesNotExist() throws Exception {
            when(serviceUseCase.update(eq(serviceId), any(UpsertServiceRequest.class)))
                    .thenThrow(new NotFoundException("Service with id " + serviceId + " not found"));

            mockMvc.perform(put("/services/{id}", serviceId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(upsertRequest)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("Not Found"))
                    .andExpect(jsonPath("$.status").value(404));
        }

        @Test
        @DisplayName("Should return 401 when user is not authenticated")
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            mockMvc.perform(put("/services/{id}", serviceId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(upsertRequest)))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(serviceUseCase);
        }
    }

    @Nested
    @DisplayName("DELETE /services/{id}")
    class Delete {

        @Test
        @DisplayName("Should delete service and return 204 when ID exists")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn204WhenServiceExists() throws Exception {
            doNothing().when(serviceUseCase).delete(serviceId);

            mockMvc.perform(delete("/services/{id}", serviceId)
                            .with(csrf()))
                    .andExpect(status().isNoContent());

            verify(serviceUseCase).delete(serviceId);
        }

        @Test
        @DisplayName("Should return 404 when service is not found")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn404WhenServiceDoesNotExist() throws Exception {
            doThrow(new NotFoundException("Service with id " + serviceId + " not found"))
                    .when(serviceUseCase).delete(serviceId);

            mockMvc.perform(delete("/services/{id}", serviceId)
                            .with(csrf()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("Not Found"))
                    .andExpect(jsonPath("$.status").value(404));
        }

        @Test
        @DisplayName("Should return 401 when user is not authenticated")
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            mockMvc.perform(delete("/services/{id}", serviceId)
                            .with(csrf()))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(serviceUseCase);
        }
    }
}

