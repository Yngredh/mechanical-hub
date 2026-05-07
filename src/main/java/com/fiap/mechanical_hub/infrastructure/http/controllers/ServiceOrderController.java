package com.fiap.mechanical_hub.infrastructure.http.controllers;

import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.domain.enums.OrderStatusEnum;
import com.fiap.mechanical_hub.domain.enums.TaskStatusEnum;
import com.fiap.mechanical_hub.infrastructure.security.UserSecurityAdapter;
import com.fiap.mechanical_hub.application.dto.serviceorder.*;
import com.fiap.mechanical_hub.application.usecases.ServiceOrderUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/service-orders")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Ordens de Serviço", description = "Endpoints para gerenciamento de ordens de serviço")
public class ServiceOrderController {

    private final ServiceOrderUseCase useCase;

    @PostMapping
    @Operation(
            summary = "Criar nova ordem de serviço",
            description = "Cria uma nova ordem de serviço para um cliente. Requer autenticação."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ordem criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "404", description = "Cliente ou veículo não encontrado")
    })
    public ResponseEntity<ServiceOrderResponse> create(
            @RequestBody CreateServiceOrderRequest request,
            @AuthenticationPrincipal UserSecurityAdapter userDetails) {
        UUID createdByUserId = userDetails.user().getId();
        ServiceOrderResponse response = useCase.create(request, createdByUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}/status")
    @Operation(
            summary = "Atualizar status da ordem de serviço",
            description = "Atualiza o status de uma ordem de serviço. Requer autenticação."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Transição de status inválida"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "404", description = "Ordem não encontrada")
    })
    public ResponseEntity<ServiceOrder> updateStatus(
            @PathVariable UUID id, @RequestBody UpdateStatusRequest request,
            @AuthenticationPrincipal UserSecurityAdapter userDetails) {
        UUID userId = userDetails.user().getId();
        ServiceOrder response = useCase.updateOrderStatus(id, OrderStatusEnum.fromString(request.getStatus()), userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(
            summary = "Listar todas as ordens de serviço",
            description = "Retorna lista de todas as ordens de serviço. Requer perfil de Administrador."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de ordens"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão (requer Administrador)")
    })
    public ResponseEntity<List<ServiceOrderSummaryResponse>> findAll() {
        return ResponseEntity.ok(useCase.findAll());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obter detalhes de uma ordem de serviço",
            description = "Retorna informações detalhadas de uma ordem específica. Requer perfil de Administrador."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalhes da ordem"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão (requer Administrador)"),
            @ApiResponse(responseCode = "404", description = "Ordem não encontrada")
    })
    public ResponseEntity<ServiceOrderDetailResponse> findById(@PathVariable UUID id) {
        ServiceOrderDetailResponse response = useCase.findById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/services")
    @Operation(
            summary = "Adicionar serviços a uma ordem",
            description = "Adiciona um ou mais serviços a uma ordem de serviço existente. Requer autenticação."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Serviços adicionados com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "404", description = "Ordem ou serviço não encontrado")
    })
    public ResponseEntity<Void> addServices(
            @PathVariable("id") UUID serviceOrderId, @Valid @RequestBody AddServicesToOrderRequest request) {
        useCase.addServices(serviceOrderId, request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/services/{taskId}/status")
    @Operation(
            summary = "Atualizar status de um serviço dentro da ordem",
            description = "Atualiza o status de um serviço específico dentro de uma ordem. Requer perfil de Mecânico."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Status do serviço atualizado"),
            @ApiResponse(responseCode = "400", description = "Transição de status inválida"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão (requer Mecânico)"),
            @ApiResponse(responseCode = "404", description = "Ordem ou serviço não encontrado")
    })
    public ResponseEntity<Void> updateTaskStatus(@PathVariable UUID id, @PathVariable UUID taskId,
                                                  @RequestBody UpdateStatusRequest request){
        useCase.updateTaskStatus(id, taskId, TaskStatusEnum.fromString(request.getStatus()));
        return ResponseEntity.noContent().build();
    }

}