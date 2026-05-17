package com.fiap.mechanical_hub.infrastructure.http.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.mechanical_hub.application.dto.vehicle.InsertVehicleRequest;
import com.fiap.mechanical_hub.application.dto.vehicle.VehicleResponse;
import com.fiap.mechanical_hub.application.usecases.AuthorizationUseCase;
import com.fiap.mechanical_hub.domain.exceptions.DuplicateLicensePlateException;
import com.fiap.mechanical_hub.domain.exceptions.InvalidLicensePlateException;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VehicleController.class)
@DisplayName("VehicleController")
class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VehicleUseCase vehicleUseCase;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private UserJpaRepository userJpaRepository;

    @MockBean
    private AuthorizationUseCase authorizationUseCase;

    private UUID vehicleId;
    private UUID customerId;
    private VehicleResponse vehicleResponse;
    private InsertVehicleRequest upsertRequest;

    @BeforeEach
    void setUp() {
        vehicleId = UUID.randomUUID();
        customerId = UUID.randomUUID();

        upsertRequest = new InsertVehicleRequest(
                "ABC-1234",
                "Toyota",
                "Corolla",
                2020,
                "Prata"
        );

        vehicleResponse = new VehicleResponse(
                vehicleId,
                customerId,
                "ABC1234",
                "Toyota",
                "Corolla",
                2020,
                "Prata",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Nested
    @DisplayName("POST /vehicles")
    class Create {

        @Test
        @DisplayName("Should create vehicle and return 201 when request data is valid")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn201WhenDataIsValid() throws Exception {
            when(vehicleUseCase.create(eq(customerId), any(InsertVehicleRequest.class))).thenReturn(vehicleResponse);

            mockMvc.perform(post("/vehicles")
                            .with(csrf())
                            .param("customer_id", customerId.toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(upsertRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(vehicleId.toString()))
                    .andExpect(jsonPath("$.customerId").value(customerId.toString()))
                    .andExpect(jsonPath("$.licensePlate").value("ABC1234"))
                    .andExpect(jsonPath("$.brand").value("Toyota"))
                    .andExpect(jsonPath("$.model").value("Corolla"))
                    .andExpect(jsonPath("$.year").value(2020))
                    .andExpect(jsonPath("$.color").value("Prata"));

            verify(vehicleUseCase).create(eq(customerId), any(InsertVehicleRequest.class));
        }

        @Test
        @DisplayName("Should return 400 when customer is not found")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn400WhenCustomerDoesNotExist() throws Exception {
            when(vehicleUseCase.create(eq(customerId), any(InsertVehicleRequest.class)))
                    .thenThrow(new NoSuchElementException("Cliente não encontrado para o id: " + customerId));

            mockMvc.perform(post("/vehicles")
                            .with(csrf())
                            .param("customer_id", customerId.toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(upsertRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.status").value(400));
        }

        @Test
        @DisplayName("Should return 409 when license plate is already registered")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn409WhenLicensePlateAlreadyExists() throws Exception {
            when(vehicleUseCase.create(eq(customerId), any(InsertVehicleRequest.class)))
                    .thenThrow(new DuplicateLicensePlateException("Veículo com placa ABC1234 já existe"));

            mockMvc.perform(post("/vehicles")
                            .with(csrf())
                            .param("customer_id", customerId.toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(upsertRequest)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.error").value("Placa duplicada"))
                    .andExpect(jsonPath("$.message").value("Veículo com placa ABC1234 já existe"));
        }

        @Test
        @DisplayName("Should return 422 when license plate format is invalid")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn422WhenLicensePlateIsInvalid() throws Exception {
            when(vehicleUseCase.create(eq(customerId), any(InsertVehicleRequest.class)))
                    .thenThrow(new InvalidLicensePlateException("Placa inválida: ABC-XXXX"));

            mockMvc.perform(post("/vehicles")
                            .with(csrf())
                            .param("customer_id", customerId.toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(upsertRequest)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.error").value("Placa inválida"))
                    .andExpect(jsonPath("$.message").value("Placa inválida: ABC-XXXX"));
        }

        @Test
        @DisplayName("Should return 401 when user is not authenticated")
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            mockMvc.perform(post("/vehicles")
                            .with(csrf())
                            .param("customer_id", customerId.toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(upsertRequest)))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(vehicleUseCase);
        }
    }

    @Nested
    @DisplayName("GET /vehicles")
    class FindAll {

        @Test
        @DisplayName("Should return 200 with vehicle list")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn200WithVehicleList() throws Exception {
            when(vehicleUseCase.findAll()).thenReturn(List.of(vehicleResponse));

            mockMvc.perform(get("/vehicles"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].id").value(vehicleId.toString()))
                    .andExpect(jsonPath("$[0].licensePlate").value("ABC1234"))
                    .andExpect(jsonPath("$[0].brand").value("Toyota"));

            verify(vehicleUseCase).findAll();
        }

        @Test
        @DisplayName("Should return 200 with empty list when no vehicles are registered")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn200WithEmptyListWhenNoVehiclesExist() throws Exception {
            when(vehicleUseCase.findAll()).thenReturn(List.of());

            mockMvc.perform(get("/vehicles"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @DisplayName("Should return 401 when user is not authenticated")
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            mockMvc.perform(get("/vehicles"))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(vehicleUseCase);
        }
    }

    @Nested
    @DisplayName("GET /vehicles/{id}")
    class FindById {

        @Test
        @DisplayName("Should return 200 with vehicle data when ID exists")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn200WhenVehicleExists() throws Exception {
            when(vehicleUseCase.findById(vehicleId)).thenReturn(vehicleResponse);

            mockMvc.perform(get("/vehicles/{id}", vehicleId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(vehicleId.toString()))
                    .andExpect(jsonPath("$.customerId").value(customerId.toString()))
                    .andExpect(jsonPath("$.licensePlate").value("ABC1234"))
                    .andExpect(jsonPath("$.brand").value("Toyota"))
                    .andExpect(jsonPath("$.model").value("Corolla"))
                    .andExpect(jsonPath("$.year").value(2020));

            verify(vehicleUseCase).findById(vehicleId);
        }

        @Test
        @DisplayName("Should return 400 when vehicle is not found")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn400WhenVehicleDoesNotExist() throws Exception {
            when(vehicleUseCase.findById(vehicleId))
                    .thenThrow(new NoSuchElementException("Veículo não encontrado para o id: " + vehicleId));

            mockMvc.perform(get("/vehicles/{id}", vehicleId))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.status").value(400));
        }

        @Test
        @DisplayName("Should return 401 when user is not authenticated")
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            mockMvc.perform(get("/vehicles/{id}", vehicleId))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(vehicleUseCase);
        }
    }

    @Nested
    @DisplayName("PUT /vehicles/{id}")
    class Update {

        @Test
        @DisplayName("Should update vehicle and return 200 when request data is valid")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn200WhenDataIsValid() throws Exception {
            when(vehicleUseCase.update(eq(vehicleId), any(InsertVehicleRequest.class)))
                    .thenReturn(vehicleResponse);

            mockMvc.perform(put("/vehicles/{id}", vehicleId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(upsertRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(vehicleId.toString()))
                    .andExpect(jsonPath("$.licensePlate").value("ABC1234"))
                    .andExpect(jsonPath("$.brand").value("Toyota"));

            verify(vehicleUseCase).update(eq(vehicleId), any(InsertVehicleRequest.class));
        }

        @Test
        @DisplayName("Should return 400 when vehicle is not found")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn400WhenVehicleDoesNotExist() throws Exception {
            when(vehicleUseCase.update(eq(vehicleId), any(InsertVehicleRequest.class)))
                    .thenThrow(new NoSuchElementException("Veículo não encontrado para o id: " + vehicleId));

            mockMvc.perform(put("/vehicles/{id}", vehicleId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(upsertRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.status").value(400));
        }

        @Test
        @DisplayName("Should return 409 when license plate already belongs to another vehicle")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn409WhenLicensePlateBelongsToAnotherVehicle() throws Exception {
            when(vehicleUseCase.update(eq(vehicleId), any(InsertVehicleRequest.class)))
                    .thenThrow(new DuplicateLicensePlateException("Veículo com placa ABC1234 já existe"));

            mockMvc.perform(put("/vehicles/{id}", vehicleId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(upsertRequest)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.error").value("Placa duplicada"))
                    .andExpect(jsonPath("$.status").value(409));
        }

        @Test
        @DisplayName("Should return 422 when license plate format is invalid")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn422WhenLicensePlateIsInvalid() throws Exception {
            when(vehicleUseCase.update(eq(vehicleId), any(InsertVehicleRequest.class)))
                    .thenThrow(new InvalidLicensePlateException("Placa inválida: ABC-XXXX"));

            mockMvc.perform(put("/vehicles/{id}", vehicleId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(upsertRequest)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.error").value("Placa inválida"))
                    .andExpect(jsonPath("$.status").value(422));
        }

        @Test
        @DisplayName("Should return 401 when user is not authenticated")
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            mockMvc.perform(put("/vehicles/{id}", vehicleId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(upsertRequest)))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(vehicleUseCase);
        }
    }

    @Nested
    @DisplayName("DELETE /vehicles/{id}")
    class Delete {

        @Test
        @DisplayName("Should delete vehicle and return 204 when ID exists")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn204WhenVehicleExists() throws Exception {
            doNothing().when(vehicleUseCase).delete(vehicleId);

            mockMvc.perform(delete("/vehicles/{id}", vehicleId)
                            .with(csrf()))
                    .andExpect(status().isNoContent());

            verify(vehicleUseCase).delete(vehicleId);
        }

        @Test
        @DisplayName("Should return 400 when vehicle is not found")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn400WhenVehicleDoesNotExist() throws Exception {
            doThrow(new NoSuchElementException("Veículo não encontrado para o id: " + vehicleId))
                    .when(vehicleUseCase).delete(vehicleId);

            mockMvc.perform(delete("/vehicles/{id}", vehicleId)
                            .with(csrf()))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.status").value(400));
        }

        @Test
        @DisplayName("Should return 401 when user is not authenticated")
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            mockMvc.perform(delete("/vehicles/{id}", vehicleId)
                            .with(csrf()))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(vehicleUseCase);
        }
    }
}