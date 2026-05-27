package com.fiap.mechanical_hub.infrastructure.http.controllers;

import com.fiap.mechanical_hub.application.command.serviceorder.ApproveServiceOrderCommand;
import com.fiap.mechanical_hub.application.command.serviceorder.FindByOrderNumberCommand;
import com.fiap.mechanical_hub.application.command.serviceorder.RejectServiceOrderCommand;
import com.fiap.mechanical_hub.application.dto.serviceorder.request.ServiceOrderCustomerView;
import com.fiap.mechanical_hub.application.usecases.serviceorder.ApproveServiceOrderUseCase;
import com.fiap.mechanical_hub.application.usecases.serviceorder.FindByOrderNumberUseCase;
import com.fiap.mechanical_hub.application.usecases.serviceorder.RejectServiceOrderUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/mechanical-hub")
@RequiredArgsConstructor
@Tag(name = "Consultas Externas", description = "Endpoints públicos para consultas e ações externas")
public class ExternalController {

    private final ApproveServiceOrderUseCase approveServiceOrderUseCase;
    private final RejectServiceOrderUseCase rejectServiceOrderUseCase;
    private final FindByOrderNumberUseCase findByOrderNumberUseCase;

    @PostMapping("/service-orders/{id}/approve")
    @Operation(
            summary = "Aprovar ordem de serviço (Externo)",
            description = "Aprova uma ordem de serviço (chamada externa/webhook). Não requer autenticação JWT."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Ordem aprovada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Operação não permitida para o status atual"),
            @ApiResponse(responseCode = "404", description = "Ordem não encontrada")
    })
    public ResponseEntity<Void> approve(@PathVariable UUID id) {
        var command = new ApproveServiceOrderCommand(id);
        approveServiceOrderUseCase.execute(command);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/service-orders/{id}/reject")
    @Operation(
            summary = "Rejeitar ordem de serviço (Externo)",
            description = "Rejeita uma ordem de serviço (chamada externa/webhook). Não requer autenticação JWT."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Ordem rejeitada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Operação não permitida para o status atual"),
            @ApiResponse(responseCode = "404", description = "Ordem não encontrada")
    })
    public ResponseEntity<Void> reject(@PathVariable UUID id) {
        var command = new RejectServiceOrderCommand(id);
        rejectServiceOrderUseCase.execute(command);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/service-orders/{orderNumber}")
    @Operation(
            summary = "Consultar ordem de serviço pelo número (Público)",
            description = "Retorna informações de uma ordem de serviço específica pelo número. Endpoint público para consultas de clientes."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Informações da ordem"),
            @ApiResponse(responseCode = "404", description = "Ordem não encontrada")
    })
    public ResponseEntity<ServiceOrderCustomerView> findByOrderNumber(@PathVariable String orderNumber) {
        var command = new FindByOrderNumberCommand(orderNumber);
        var response = findByOrderNumberUseCase.execute(command);
        return ResponseEntity.ok(response);
    }
}