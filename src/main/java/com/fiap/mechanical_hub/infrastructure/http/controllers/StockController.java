package com.fiap.mechanical_hub.infrastructure.http.controllers;

import com.fiap.mechanical_hub.application.dto.stock.StockDetailResponse;
import com.fiap.mechanical_hub.application.dto.stock.StockEntryRequest;
import com.fiap.mechanical_hub.application.dto.stock.StockSummaryResponse;
import com.fiap.mechanical_hub.application.usecases.StockUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/stock")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Estoque", description = "Endpoints para gerenciamento de estoque de materiais")
public class StockController {

    private final StockUseCase stockUseCase;

    @PostMapping("/entry")
    @Operation(
            summary = "Registrar entrada de material",
            description = "Registra a entrada de novos materiais no estoque. Requer perfil de Mecânico ou Administrador."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Entrada registrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão (requer Mecânico ou Administrador)"),
            @ApiResponse(responseCode = "404", description = "Material não encontrado")
    })
    public ResponseEntity<Void> registerEntry(@Valid @RequestBody StockEntryRequest request) {
        stockUseCase.registerStockEntry(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(
            summary = "Listar resumo do estoque",
            description = "Retorna um resumo do estoque de todos os materiais. Requer autenticação."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resumo do estoque"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão (requer Mecânico ou Administrador)")
    })
    public ResponseEntity<List<StockSummaryResponse>> findAll() {
        List<StockSummaryResponse> stockSummaries = stockUseCase.findAll();
        return ResponseEntity.ok(stockSummaries);
    }

    @GetMapping("/{materialId}")
    @Operation(
            summary = "Obter detalhes do estoque de um material",
            description = "Retorna informações detalhadas do estoque de um material específico. Requer autenticação."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalhes do estoque"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão (requer Mecânico ou Administrador)"),
            @ApiResponse(responseCode = "404", description = "Material não encontrado")
    })
    public ResponseEntity<StockDetailResponse> findByMaterialId(@PathVariable UUID materialId) {
        StockDetailResponse stockDetail = stockUseCase.findByMaterialId(materialId);
        return ResponseEntity.ok(stockDetail);
    }

    @DeleteMapping("/{materialId}")
    @Operation(summary = "Deletar item de estoque", description = "Deleta um item de estoque do sistema. Requer perfil de Administrador.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Item de estoque deletado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão (requer Administrador)"),
            @ApiResponse(responseCode = "404", description = "Item de estoque não encontrado")
    })
    public ResponseEntity<Void> delete(@PathVariable UUID materialId) {
        stockUseCase.delete(materialId);
        return ResponseEntity.noContent().build();
    }
}
