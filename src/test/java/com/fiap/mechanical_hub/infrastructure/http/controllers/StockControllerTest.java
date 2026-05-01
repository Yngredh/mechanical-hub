package com.fiap.mechanical_hub.infrastructure.http.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.mechanical_hub.application.dto.stock.StockDetailResponse;
import com.fiap.mechanical_hub.application.dto.stock.StockEntryRequest;
import com.fiap.mechanical_hub.application.dto.stock.StockMovementResponse;
import com.fiap.mechanical_hub.application.dto.stock.StockSummaryResponse;
import com.fiap.mechanical_hub.application.usecases.AuthorizationUseCase;
import com.fiap.mechanical_hub.application.usecases.StockUseCase;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StockController.class)
@DisplayName("StockController")
class StockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StockUseCase stockUseCase;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private UserJpaRepository userJpaRepository;

    @MockBean
    private AuthorizationUseCase authorizationUseCase;

    private UUID materialId;
    private UUID movementId;
    private UUID serviceOrderId;
    private StockEntryRequest stockEntryRequest;
    private StockSummaryResponse stockSummaryResponse;
    private StockDetailResponse stockDetailResponse;

    @BeforeEach
    void setUp() {
        materialId = UUID.randomUUID();
        movementId = UUID.randomUUID();
        serviceOrderId = UUID.randomUUID();

        stockEntryRequest = new StockEntryRequest(materialId, 5);

        stockSummaryResponse = new StockSummaryResponse(
                materialId,
                "Oil Filter",
                20,
                15,
                5
        );

        StockMovementResponse movementResponse = new StockMovementResponse(
                movementId,
                materialId,
                serviceOrderId,
                "ENTRY",
                5,
                LocalDateTime.now()
        );

        stockDetailResponse = new StockDetailResponse(
                materialId,
                20,
                15,
                5,
                List.of(movementResponse)
        );
    }

    @Nested
    @DisplayName("POST /stock/entry")
    class RegisterEntry {

        @Test
        @DisplayName("Should register stock entry and return 204 when request is valid")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn204WhenDataIsValid() throws Exception {
            doNothing().when(stockUseCase).registerStockEntry(any(StockEntryRequest.class));

            mockMvc.perform(post("/stock/entry")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(stockEntryRequest)))
                    .andExpect(status().isNoContent());

            verify(stockUseCase).registerStockEntry(any(StockEntryRequest.class));
        }

        @Test
        @DisplayName("Should return 404 when material is not found")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn404WhenMaterialNotFound() throws Exception {
            doThrow(new NotFoundException("Estoque nao encontrado para o material id: " + materialId))
                    .when(stockUseCase).registerStockEntry(any(StockEntryRequest.class));

            mockMvc.perform(post("/stock/entry")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(stockEntryRequest)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("Not Found"))
                    .andExpect(jsonPath("$.status").value(404));
        }

        @Test
        @DisplayName("Should return 401 when user is not authenticated")
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            mockMvc.perform(post("/stock/entry")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(stockEntryRequest)))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(stockUseCase);
        }
    }

    @Nested
    @DisplayName("GET /stock")
    class FindAll {

        @Test
        @DisplayName("Should return 200 with stock summary list")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn200WithList() throws Exception {
            when(stockUseCase.findAll()).thenReturn(List.of(stockSummaryResponse));

            mockMvc.perform(get("/stock"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].materialId").value(materialId.toString()))
                    .andExpect(jsonPath("$[0].quantityTotal").value(20));

            verify(stockUseCase).findAll();
        }

        @Test
        @DisplayName("Should return 200 with empty list when no stock exists")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn200WithEmptyListWhenNoStockExists() throws Exception {
            when(stockUseCase.findAll()).thenReturn(List.of());

            mockMvc.perform(get("/stock"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @DisplayName("Should return 401 when user is not authenticated")
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            mockMvc.perform(get("/stock"))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(stockUseCase);
        }
    }

    @Nested
    @DisplayName("GET /stock/{materialId}")
    class FindByMaterialId {

        @Test
        @DisplayName("Should return 200 with stock details when material exists")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn200WhenMaterialExists() throws Exception {
            when(stockUseCase.findByMaterialId(materialId)).thenReturn(stockDetailResponse);

            mockMvc.perform(get("/stock/{materialId}", materialId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.materialId").value(materialId.toString()))
                    .andExpect(jsonPath("$.quantityAvailable").value(15))
                    .andExpect(jsonPath("$.movements").isArray())
                    .andExpect(jsonPath("$.movements.length()").value(1))
                    .andExpect(jsonPath("$.movements[0].movementType").value("ENTRY"));

            verify(stockUseCase).findByMaterialId(materialId);
        }

        @Test
        @DisplayName("Should return 404 when material is not found")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn404WhenMaterialDoesNotExist() throws Exception {
            when(stockUseCase.findByMaterialId(materialId))
                    .thenThrow(new NotFoundException("Estoque nao encontrado para o material id: " + materialId));

            mockMvc.perform(get("/stock/{materialId}", materialId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("Not Found"))
                    .andExpect(jsonPath("$.status").value(404));
        }

        @Test
        @DisplayName("Should return 401 when user is not authenticated")
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            mockMvc.perform(get("/stock/{materialId}", materialId))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(stockUseCase);
        }
    }
}

