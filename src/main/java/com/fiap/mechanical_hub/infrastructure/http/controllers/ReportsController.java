package com.fiap.mechanical_hub.infrastructure.http.controllers;

import com.fiap.mechanical_hub.application.dto.reports.AverageServiceExecutionTime;
import com.fiap.mechanical_hub.application.usecases.ReportUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Relatórios", description = "Endpoints para gerar relatórios de desempenho")
public class ReportsController {

    private final ReportUseCase reportUseCase;

    @GetMapping("/execution-time")
    @Operation(
            summary = "Obter tempo médio de execução dos serviços",
            description = "Retorna o tempo médio de execução de cada tipo de serviço. Requer perfil de Administrador."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Relatório de tempos de execução"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão (requer Administrador)")
    })
    public ResponseEntity<List<AverageServiceExecutionTime>> getAverageExecutionTime() {
        List<AverageServiceExecutionTime> executionTimes = reportUseCase.getAverageExecutionTime();
        return ResponseEntity.ok(executionTimes);
    }
}
