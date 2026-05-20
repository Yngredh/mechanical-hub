package com.fiap.mechanical_hub.application.usecases;

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
public class ReportUseCase {

    private final OrderTaskRepository orderTaskRepository;

    public List<AverageServiceExecutionTime> getAverageExecutionTime() {
        return orderTaskRepository.findAverageExecutionTimeByService()
                .stream()
                .map(this::buildAverageExecutionTimeResponse).toList();
    }

    private AverageServiceExecutionTime buildAverageExecutionTimeResponse(Object[] service) {
        UUID serviceId = (UUID) service[0];
        String serviceName = (String) service[1];
        Long avgExecutionMinutes = ((Number) service[2]).longValue();
        Long totalExecutions = ((Number) service[3]).longValue();

        return new AverageServiceExecutionTime(
                serviceId,
                serviceName,
                avgExecutionMinutes,
                totalExecutions
        );
    }
}

