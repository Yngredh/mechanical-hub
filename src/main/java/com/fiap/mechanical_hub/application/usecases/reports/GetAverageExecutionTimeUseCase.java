package com.fiap.mechanical_hub.application.usecases.reports;

import com.fiap.mechanical_hub.application.dto.reports.AverageServiceExecutionTime;
import com.fiap.mechanical_hub.domain.repositories.OrderTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetAverageExecutionTimeUseCase {
    private final OrderTaskRepository orderTaskRepository;

    public List<AverageServiceExecutionTime> getAverageExecutionTime() {
        return orderTaskRepository.findAverageExecutionTimeByService()
                .stream()
                .map(this::buildReport)
                .toList();
    }

    private AverageServiceExecutionTime buildReport(Object[] result) {
        if (result == null || result.length < 4) {
            throw new IllegalArgumentException("Invalid query result for execution time mapping");
        }

        UUID serviceId = (UUID) result[0];
        String serviceName = (String) result[1];
        Long avgExecutionMinutes = ((Number) result[2]).longValue();
        Long totalExecutions = ((Number) result[3]).longValue();

        return new AverageServiceExecutionTime(
                serviceId,
                serviceName,
                avgExecutionMinutes,
                totalExecutions
        );
    }
}
