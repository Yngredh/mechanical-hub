package com.fiap.mechanical_hub.infrastructure.http.controllers;

import com.fiap.mechanical_hub.application.command.ordertask.CreateServiceCommand;
import com.fiap.mechanical_hub.application.command.ordertask.DeleteOrderTaskCommand;
import com.fiap.mechanical_hub.application.command.ordertask.FindOrderTaskByIdCommand;
import com.fiap.mechanical_hub.application.command.ordertask.UpdateServiceCommand;
import com.fiap.mechanical_hub.application.dto.service.ServiceResponse;
import com.fiap.mechanical_hub.application.dto.service.UpsertServiceRequest;
import com.fiap.mechanical_hub.application.usecases.service.CreateServiceUseCase;
import com.fiap.mechanical_hub.application.usecases.service.DeleteServiceUseCase;
import com.fiap.mechanical_hub.application.usecases.service.FindAllServicesUseCase;
import com.fiap.mechanical_hub.application.usecases.service.FindServiceByIdUseCase;
import com.fiap.mechanical_hub.application.usecases.service.UpdateServiceUseCase;
import com.fiap.mechanical_hub.application.mappers.ServiceMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Serviços", description = "Endpoints para gerenciamento de serviços disponíveis")
public class ServiceController {

    private final CreateServiceUseCase createServiceUseCase;
    private final UpdateServiceUseCase updateServiceUseCase;
    private final FindServiceByIdUseCase findServiceByIdUseCase;
    private final FindAllServicesUseCase findAllServicesUseCase;
    private final DeleteServiceUseCase deleteServiceUseCase;

    @PostMapping
    @Operation(summary = "Criar novo serviço", description = "Cria um novo tipo de serviço no sistema. Requer perfil de Administrador.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Serviço criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão (requer Administrador)")
    })
    public ResponseEntity<ServiceResponse> create(@RequestBody @Valid UpsertServiceRequest request) {
        CreateServiceCommand command = ServiceMapper.toCreateCommand(request);
        ServiceResponse response = createServiceUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar todos os serviços", description = "Retorna lista de todos os tipos de serviços disponíveis. Requer autenticação.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de serviços"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão (requer Administrador)")
    })
    public ResponseEntity<List<ServiceResponse>> findAll() {
        List<ServiceResponse> services = findAllServicesUseCase.execute();
        return ResponseEntity.ok(services);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter serviço por ID", description = "Retorna os detalhes de um serviço específico. Requer autenticação.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Serviço encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão (requer Administrador)"),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado")
    })
    public ResponseEntity<ServiceResponse> findById(@PathVariable UUID id) {
        FindOrderTaskByIdCommand command = new FindOrderTaskByIdCommand(id);
        ServiceResponse response = findServiceByIdUseCase.execute(command);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar serviço", description = "Atualiza os dados de um serviço. Requer perfil de Administrador.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Serviço atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão (requer Administrador)"),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado")
    })
    public ResponseEntity<ServiceResponse> update(
            @PathVariable UUID id,
            @RequestBody @Valid UpsertServiceRequest request
    ) {
        UpdateServiceCommand command = ServiceMapper.toUpdateCommand(id, request);
        ServiceResponse response = updateServiceUseCase.execute(command);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar serviço", description = "Deleta um serviço do sistema. Requer perfil de Administrador.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Serviço deletado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão (requer Administrador)"),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado")
    })
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        DeleteOrderTaskCommand command = new DeleteOrderTaskCommand(id);
        deleteServiceUseCase.execute(command);
        return ResponseEntity.noContent().build();
    }
}
