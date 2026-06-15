package com.fiap.mechanical_hub.infrastructure.http.controllers;

import com.fiap.mechanical_hub.application.dto.reports.AverageServiceExecutionTime;
import com.fiap.mechanical_hub.application.usecases.reports.GetAverageExecutionTimeUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReportsControllerTest {

    private final GetAverageExecutionTimeUseCase getAverageExecutionTimeUseCase = mock(GetAverageExecutionTimeUseCase.class);

    private final ReportsController controller = new ReportsController(getAverageExecutionTimeUseCase);

    @Test
    void shouldReturnOk_whenGettingAverageExecutionTime() {
        AverageServiceExecutionTime report = new AverageServiceExecutionTime(
                UUID.fromString("00000000-0000-0000-0000-000000000001"), "Troca de óleo", 60L, 5L
        );
        when(getAverageExecutionTimeUseCase.getAverageExecutionTime()).thenReturn(List.of(report));

        ResponseEntity<List<AverageServiceExecutionTime>> response = controller.getAverageExecutionTime();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void shouldReturnEmptyList_whenNoExecutionDataExists() {
        when(getAverageExecutionTimeUseCase.getAverageExecutionTime()).thenReturn(List.of());

        ResponseEntity<List<AverageServiceExecutionTime>> response = controller.getAverageExecutionTime();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void shouldDelegateToUseCase_whenGettingAverageExecutionTime() {
        when(getAverageExecutionTimeUseCase.getAverageExecutionTime()).thenReturn(List.of());

        controller.getAverageExecutionTime();

        verify(getAverageExecutionTimeUseCase).getAverageExecutionTime();
    }
}
