package com.fiap.mechanical_hub.infrastructure.http.controllers;

import com.fiap.mechanical_hub.domain.enums.OrderStatusEnum;
import com.fiap.mechanical_hub.domain.enums.TaskStatusEnum;
import com.fiap.mechanical_hub.infrastructure.http.mappers.ServiceOrderHttpMapper;
import com.fiap.mechanical_hub.infrastructure.security.UserSecurityAdapter;
import com.fiap.mechanical_hub.application.command.serviceorder.*;
import com.fiap.mechanical_hub.application.dto.serviceorder.*;
import com.fiap.mechanical_hub.application.usecases.serviceorder.*;
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

    private final CreateServiceOrderUseCase createServiceOrderUseCase;
    private final OpenServiceOrderUseCase openServiceOrderUseCase;
    private final AddTaskIntoServiceOrderUseCase addTaskIntoServiceOrderUseCase;
    private final UpdateServiceOrderStatusUseCase updateServiceOrderStatusUseCase;
    private final FindAllServiceOrderUseCase findAllServiceOrderUseCase;
    private final FindServiceOrderByIdUseCase findServiceOrderByIdUseCase;
    private final UpdateTaskStatusUseCase updateTaskStatusUseCase;
    private final ServiceOrderHttpMapper mapper;

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
        var command = mapper.toCreateServiceOrderCommand(request, createdByUserId);
        ServiceOrderResponse response = createServiceOrderUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/open")
    @Operation(
            summary = "Abrir ordem de serviço unificada",
            description = "Abre uma nova ordem de serviço fornecendo IDs de cliente, veículo e serviços já existentes. " +
                    "Os serviços devem ter suas peças já associadas. Requer autenticação."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ordem aberta com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou veículo não pertence ao cliente"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "404", description = "Cliente, veículo ou serviço não encontrado")
    })
    public ResponseEntity<ServiceOrderResponse> open(
            @Valid @RequestBody OpenServiceOrderRequest request,
            @AuthenticationPrincipal UserSecurityAdapter userDetails) {
        log.info("Opening service order with customer: {} | vehicle: {} | services count: {}",
                request.getCustomerId(), request.getVehicleId(), request.getServiceIds().size());
        UUID createdByUserId = userDetails.user().getId();
        var command = mapper.toOpenServiceOrderCommand(request, createdByUserId);
        ServiceOrderResponse response = openServiceOrderUseCase.execute(command);
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
    public ResponseEntity<Void> updateStatus(
            @PathVariable UUID id, @RequestBody UpdateStatusRequest request,
            @AuthenticationPrincipal UserSecurityAdapter userDetails) {
        UUID userId = userDetails.user().getId();
        var command = new UpdateServiceOrderStatusCommand(id, OrderStatusEnum.fromString(request.getStatus()), userId);
        updateServiceOrderStatusUseCase.execute(command);
        return ResponseEntity.ok().build();
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
        return ResponseEntity.ok(findAllServiceOrderUseCase.execute());
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
        ServiceOrderDetailResponse response = findServiceOrderByIdUseCase.execute(id);
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
        var command = new AddTaskIntoServiceOrderCommand(serviceOrderId, request.serviceIds());
        addTaskIntoServiceOrderUseCase.execute(command);
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
        var command = new UpdateTaskStatusCommand(id, taskId, TaskStatusEnum.fromString(request.getStatus()));
        updateTaskStatusUseCase.execute(command);
        return ResponseEntity.noContent().build();
    }

}