package com.fiap.mechanical_hub.infrastructure.http.controllers;

import com.fiap.mechanical_hub.application.dto.material.InsertMaterialRequest;
import com.fiap.mechanical_hub.application.dto.material.MaterialResponse;
import com.fiap.mechanical_hub.application.dto.material.UpdateMaterialRequest;
import com.fiap.mechanical_hub.application.mappers.MaterialMapper;
import com.fiap.mechanical_hub.application.usecases.material.CreateMaterialUseCase;
import com.fiap.mechanical_hub.application.usecases.material.DeleteMaterialUseCase;
import com.fiap.mechanical_hub.application.usecases.material.FindAllMaterialsUseCase;
import com.fiap.mechanical_hub.application.usecases.material.FindMaterialByIdUseCase;
import com.fiap.mechanical_hub.application.usecases.material.UpdateMaterialUseCase;
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
@RequestMapping("/materials")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Materiais", description = "Endpoints para gerenciamento de materiais/peças")
public class MaterialController {

    private final MaterialMapper mapper;
    private final CreateMaterialUseCase createMaterialUseCase;
    private final FindMaterialByIdUseCase findMaterialByIdUseCase;
    private final FindAllMaterialsUseCase findAllMaterialsUseCase;
    private final UpdateMaterialUseCase updateMaterialUseCase;
    private final DeleteMaterialUseCase deleteMaterialUseCase;

    @PostMapping
    @Operation(summary = "Criar novo material", description = "Cria um novo material/peça no catálogo. Requer perfil de Administrador.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Material criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão (requer Administrador)")
    })
    public ResponseEntity<MaterialResponse> create(@Valid @RequestBody InsertMaterialRequest request) {
        var command = mapper.toCreateCommand(request);
        var response = createMaterialUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar todos os materiais", description = "Retorna lista de todos os materiais cadastrados. Requer autenticação.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de materiais"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão (requer Administrador)")
    })
    public ResponseEntity<List<MaterialResponse>> findAll() {
        List<MaterialResponse> materials = findAllMaterialsUseCase.execute();
        return ResponseEntity.ok(materials);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter material por ID", description = "Retorna os detalhes de um material específico. Requer autenticação.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Material encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão (requer Administrador)"),
            @ApiResponse(responseCode = "404", description = "Material não encontrado")
    })
    public ResponseEntity<MaterialResponse> findById(@PathVariable UUID id) {
        var response = findMaterialByIdUseCase.execute(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar material", description = "Atualiza os dados de um material. Requer perfil de Administrador.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Material atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão (requer Administrador)"),
            @ApiResponse(responseCode = "404", description = "Material não encontrado")
    })
    public ResponseEntity<MaterialResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateMaterialRequest request) {
        var command = mapper.toUpdateCommand(id, request);
        var response = updateMaterialUseCase.execute(command);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar material", description = "Deleta um material do sistema. Requer perfil de Administrador.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Material deletado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão (requer Administrador)"),
            @ApiResponse(responseCode = "404", description = "Material não encontrado")
    })
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deleteMaterialUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
