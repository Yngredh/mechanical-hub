package com.fiap.mechanical_hub.infrastructure.http.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.mechanical_hub.application.dto.material.MaterialResponse;
import com.fiap.mechanical_hub.application.dto.material.UpsertMaterialRequest;
import com.fiap.mechanical_hub.application.usecases.AuthorizationUseCase;
import com.fiap.mechanical_hub.application.usecases.MaterialUseCase;
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

@WebMvcTest(MaterialController.class)
@DisplayName("MaterialController")
class MaterialControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MaterialUseCase materialUseCase;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private UserJpaRepository userJpaRepository;

    @MockBean
    private AuthorizationUseCase authorizationUseCase;

    private UUID materialId;
    private MaterialResponse materialResponse;
    private UpsertMaterialRequest upsertRequest;

    @BeforeEach
    void setUp() {
        materialId = UUID.randomUUID();

        upsertRequest = new UpsertMaterialRequest(
                "Filtro de Oleo",
                "Filtro para motor 1.6",
                new BigDecimal("45.90"),
                5
        );

        materialResponse = new MaterialResponse(
                materialId,
                "Filtro de Oleo",
                "Filtro para motor 1.6",
                new BigDecimal("45.90"),
                5,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Nested
    @DisplayName("POST /materials")
    class Create {

        @Test
        @DisplayName("Should create material and return 201 when request data is valid")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn201WhenDataIsValid() throws Exception {
            when(materialUseCase.create(any(UpsertMaterialRequest.class))).thenReturn(materialResponse);

            mockMvc.perform(post("/materials")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(upsertRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(materialId.toString()))
                    .andExpect(jsonPath("$.name").value("Filtro de Oleo"))
                    .andExpect(jsonPath("$.description").value("Filtro para motor 1.6"))
                    .andExpect(jsonPath("$.unitPrice").value(45.90))
                    .andExpect(jsonPath("$.minStockQuantity").value(5));

            verify(materialUseCase).create(any(UpsertMaterialRequest.class));
        }

        @Test
        @DisplayName("Should return 401 when user is not authenticated")
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            mockMvc.perform(post("/materials")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(upsertRequest)))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(materialUseCase);
        }
    }

    @Nested
    @DisplayName("GET /materials")
    class FindAll {

        @Test
        @DisplayName("Should return 200 with material list")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn200WithMaterialList() throws Exception {
            when(materialUseCase.findAll()).thenReturn(List.of(materialResponse));

            mockMvc.perform(get("/materials"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].id").value(materialId.toString()))
                    .andExpect(jsonPath("$[0].name").value("Filtro de Oleo"));

            verify(materialUseCase).findAll();
        }

        @Test
        @DisplayName("Should return 200 with empty list when no materials are registered")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn200WithEmptyListWhenNoMaterialsExist() throws Exception {
            when(materialUseCase.findAll()).thenReturn(List.of());

            mockMvc.perform(get("/materials"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @DisplayName("Should return 401 when user is not authenticated")
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            mockMvc.perform(get("/materials"))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(materialUseCase);
        }
    }

    @Nested
    @DisplayName("GET /materials/{id}")
    class FindById {

        @Test
        @DisplayName("Should return 200 with material data when ID exists")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn200WhenMaterialExists() throws Exception {
            when(materialUseCase.findMaterialById(materialId)).thenReturn(materialResponse);

            mockMvc.perform(get("/materials/{id}", materialId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(materialId.toString()))
                    .andExpect(jsonPath("$.name").value("Filtro de Oleo"))
                    .andExpect(jsonPath("$.unitPrice").value(45.90));

            verify(materialUseCase).findMaterialById(materialId);
        }

        @Test
        @DisplayName("Should return 404 when material is not found")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn404WhenMaterialDoesNotExist() throws Exception {
            when(materialUseCase.findMaterialById(materialId))
                    .thenThrow(new NotFoundException("Material não encontrado para o id: " + materialId));

            mockMvc.perform(get("/materials/{id}", materialId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("Not Found"))
                    .andExpect(jsonPath("$.status").value(404));
        }

        @Test
        @DisplayName("Should return 401 when user is not authenticated")
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            mockMvc.perform(get("/materials/{id}", materialId))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(materialUseCase);
        }
    }

    @Nested
    @DisplayName("PUT /materials/{id}")
    class Update {

        @Test
        @DisplayName("Should update material and return 200 when request data is valid")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn200WhenDataIsValid() throws Exception {
            when(materialUseCase.update(eq(materialId), any(UpsertMaterialRequest.class)))
                    .thenReturn(materialResponse);

            mockMvc.perform(put("/materials/{id}", materialId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(upsertRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(materialId.toString()))
                    .andExpect(jsonPath("$.name").value("Filtro de Oleo"));

            verify(materialUseCase).update(eq(materialId), any(UpsertMaterialRequest.class));
        }

        @Test
        @DisplayName("Should return 404 when material is not found")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn404WhenMaterialDoesNotExist() throws Exception {
            when(materialUseCase.update(eq(materialId), any(UpsertMaterialRequest.class)))
                    .thenThrow(new NotFoundException("Material não encontrado para o id: " + materialId));

            mockMvc.perform(put("/materials/{id}", materialId)
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
            mockMvc.perform(put("/materials/{id}", materialId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(upsertRequest)))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(materialUseCase);
        }
    }

    @Nested
    @DisplayName("DELETE /materials/{id}")
    class Delete {

        @Test
        @DisplayName("Should delete material and return 204 when ID exists")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn204WhenMaterialExists() throws Exception {
            doNothing().when(materialUseCase).delete(materialId);

            mockMvc.perform(delete("/materials/{id}", materialId)
                            .with(csrf()))
                    .andExpect(status().isNoContent());

            verify(materialUseCase).delete(materialId);
        }

        @Test
        @DisplayName("Should return 404 when material is not found")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn404WhenMaterialDoesNotExist() throws Exception {
            doThrow(new NotFoundException("Material não encontrado para o id: " + materialId))
                    .when(materialUseCase).delete(materialId);

            mockMvc.perform(delete("/materials/{id}", materialId)
                            .with(csrf()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("Not Found"))
                    .andExpect(jsonPath("$.status").value(404));
        }

        @Test
        @DisplayName("Should return 401 when user is not authenticated")
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            mockMvc.perform(delete("/materials/{id}", materialId)
                            .with(csrf()))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(materialUseCase);
        }
    }
}

