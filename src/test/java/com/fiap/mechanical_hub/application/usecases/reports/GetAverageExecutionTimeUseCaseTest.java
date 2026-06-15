package com.fiap.mechanical_hub.application.usecases.reports;

import com.fiap.mechanical_hub.application.dto.reports.AverageServiceExecutionTime;
import com.fiap.mechanical_hub.domain.repositories.OrderTaskRepository;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetAverageExecutionTimeUseCaseTest {

    private static final UUID SERVICE_ID = UUID.fromString("00000000-0000-0000-0000-000000000040");

    private final OrderTaskRepository orderTaskRepository = mock(OrderTaskRepository.class);
    private final GetAverageExecutionTimeUseCase useCase = new GetAverageExecutionTimeUseCase(orderTaskRepository);

    @Test
    void shouldReturnMappedReport_whenRepositoryReturnsResults() {
        Object[] row = {SERVICE_ID, "Troca de óleo", 45L, 10L};
        List<Object[]> rows = new ArrayList<>();
        rows.add(row);
        when(orderTaskRepository.findAverageExecutionTimeByService()).thenReturn(rows);

        List<AverageServiceExecutionTime> result = useCase.getAverageExecutionTime();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getServiceId()).isEqualTo(SERVICE_ID);
        assertThat(result.getFirst().getServiceName()).isEqualTo("Troca de óleo");
        assertThat(result.getFirst().getAvgExecutionMinutes()).isEqualTo(45L);
        assertThat(result.getFirst().getTotalExecutions()).isEqualTo(10L);
    }

    @Test
    void shouldReturnEmptyList_whenNoResultsExist() {
        when(orderTaskRepository.findAverageExecutionTimeByService()).thenReturn(List.of());

        List<AverageServiceExecutionTime> result = useCase.getAverageExecutionTime();

        assertThat(result).isEmpty();
    }

    @Test
    void shouldThrowIllegalArgumentException_whenQueryResultIsInvalid() {
        Object[] invalidRow = {SERVICE_ID, "Troca de óleo"};
        List<Object[]> invalidRows = new ArrayList<>();
        invalidRows.add(invalidRow);
        when(orderTaskRepository.findAverageExecutionTimeByService()).thenReturn(invalidRows);

        assertThatThrownBy(useCase::getAverageExecutionTime)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid query result");
    }
}
