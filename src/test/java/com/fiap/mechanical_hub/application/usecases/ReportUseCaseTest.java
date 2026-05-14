package com.fiap.mechanical_hub.application.usecases;

import com.fiap.mechanical_hub.application.dto.reports.ServiceExecutionTimeResponse;
import com.fiap.mechanical_hub.domain.repositories.OrderTaskRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportUseCaseTest {

    @Mock
    private OrderTaskRepository orderTaskRepository;

    @InjectMocks
    private ReportUseCase reportUseCase;

    @Test
    @DisplayName("Deve retornar a lista de tempo médio de execução com sucesso")
    void shouldReturnAverageExecutionTimeList() {
        UUID serviceId = UUID.randomUUID();
        Object[] row = new Object[]{
                serviceId,
                "Troca de Óleo",
                45L,
                10L
        };

        when(orderTaskRepository.findAverageExecutionTimeByService())
                .thenReturn(Collections.singletonList(row));

        List<ServiceExecutionTimeResponse> result = reportUseCase.getAverageExecutionTime();

        assertThat(result).hasSize(1);
        ServiceExecutionTimeResponse response = result.get(0);

        assertThat(response.getServiceId()).isEqualTo(serviceId);
        assertThat(response.getServiceName()).isEqualTo("Troca de Óleo");
        assertThat(response.getAvgExecutionMinutes()).isEqualTo(45L);
        assertThat(response.getTotalExecutions()).isEqualTo(10L);

        verify(orderTaskRepository, times(1)).findAverageExecutionTimeByService();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver dados no repositório")
    void shouldReturnEmptyListWhenNoData() {
        when(orderTaskRepository.findAverageExecutionTimeByService())
                .thenReturn(Collections.emptyList());

        List<ServiceExecutionTimeResponse> result = reportUseCase.getAverageExecutionTime();

        assertThat(result).isEmpty();
    }
}