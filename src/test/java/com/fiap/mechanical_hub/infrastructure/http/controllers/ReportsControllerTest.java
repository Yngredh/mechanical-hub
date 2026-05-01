package com.fiap.mechanical_hub.infrastructure.http.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.mechanical_hub.application.dto.reports.ServiceExecutionTimeResponse;
import com.fiap.mechanical_hub.application.usecases.AuthorizationUseCase;
import com.fiap.mechanical_hub.application.usecases.ReportUseCase;
import com.fiap.mechanical_hub.infrastructure.database.repositories.UserJpaRepository;
import com.fiap.mechanical_hub.infrastructure.security.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReportsController.class)
@DisplayName("ReportsController")
class ReportsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReportUseCase reportUseCase;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private UserJpaRepository userJpaRepository;

    @MockBean
    private AuthorizationUseCase authorizationUseCase;

    private UUID serviceId;
    private ServiceExecutionTimeResponse executionTimeResponse;

    @BeforeEach
    void setUp() {
        serviceId = UUID.randomUUID();
        executionTimeResponse = new ServiceExecutionTimeResponse(
                serviceId,
                "Oil Change",
                90L,
                12L
        );
    }

    @Nested
    @DisplayName("GET /reports/execution-time")
    class GetAverageExecutionTime {

        @Test
        @DisplayName("Should return 200 with execution time report")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn200WithReport() throws Exception {
            when(reportUseCase.getAverageExecutionTime()).thenReturn(List.of(executionTimeResponse));

            mockMvc.perform(get("/reports/execution-time"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].serviceId").value(serviceId.toString()))
                    .andExpect(jsonPath("$[0].serviceName").value("Oil Change"))
                    .andExpect(jsonPath("$[0].avgExecutionMinutes").value(90))
                    .andExpect(jsonPath("$[0].totalExecutions").value(12));

            verify(reportUseCase).getAverageExecutionTime();
        }

        @Test
        @DisplayName("Should return 200 with empty list when no data exists")
        @WithMockUser(roles = "ADMINISTRATOR")
        void shouldReturn200WithEmptyListWhenNoData() throws Exception {
            when(reportUseCase.getAverageExecutionTime()).thenReturn(List.of());

            mockMvc.perform(get("/reports/execution-time"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @DisplayName("Should return 401 when user is not authenticated")
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            mockMvc.perform(get("/reports/execution-time"))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(reportUseCase);
        }
    }
}

