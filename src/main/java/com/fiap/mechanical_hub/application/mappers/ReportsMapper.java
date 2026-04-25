package com.fiap.mechanical_hub.application.mappers;

import com.fiap.mechanical_hub.application.dto.reports.ServiceExecutionTimeResponse;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ReportsMapper {
    private ReportsMapper() {}

    public static ServiceExecutionTimeResponse toResponse(Object[] result) {
        if (result == null || result.length < 4) {
            throw new IllegalArgumentException("Invalid query result for execution time mapping");
        }

        UUID serviceId = (UUID) result[0];
        String serviceName = (String) result[1];
        Long avgExecutionMinutes = ((Number) result[2]).longValue();
        Long totalExecutions = ((Number) result[3]).longValue();

        return new ServiceExecutionTimeResponse(
                serviceId,
                serviceName,
                avgExecutionMinutes,
                totalExecutions
        );
    }
}

