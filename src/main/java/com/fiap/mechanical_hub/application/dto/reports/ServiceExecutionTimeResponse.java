package com.fiap.mechanical_hub.application.dto.reports;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ServiceExecutionTimeResponse {

    private UUID serviceId;
    private String serviceName;
    private Long avgExecutionMinutes;
    private Long totalExecutions;
}

