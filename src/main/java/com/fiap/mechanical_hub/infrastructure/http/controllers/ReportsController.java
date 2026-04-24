package com.fiap.mechanical_hub.infrastructure.http.controllers;

import com.fiap.mechanical_hub.application.dto.reports.ServiceExecutionTimeResponse;
import com.fiap.mechanical_hub.application.usecases.ServiceExecutionTimeUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportsController {

    private final ServiceExecutionTimeUseCase serviceExecutionTimeUseCase;

    @GetMapping("/execution-time")
    public ResponseEntity<List<ServiceExecutionTimeResponse>> getAverageExecutionTime() {
        List<ServiceExecutionTimeResponse> executionTimes = serviceExecutionTimeUseCase.getAverageExecutionTime();
        return ResponseEntity.ok(executionTimes);
    }
}
