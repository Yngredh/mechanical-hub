package com.fiap.mechanical_hub.infrastructure.http.controllers;

import com.fiap.mechanical_hub.application.dto.reports.ServiceExecutionTimeResponse;
import com.fiap.mechanical_hub.application.usecases.ReportUseCase;
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

    private final ReportUseCase reportUseCase;

    @GetMapping("/execution-time")
    public ResponseEntity<List<ServiceExecutionTimeResponse>> getAverageExecutionTime() {
        List<ServiceExecutionTimeResponse> executionTimes = reportUseCase.getAverageExecutionTime();
        return ResponseEntity.ok(executionTimes);
    }
}
