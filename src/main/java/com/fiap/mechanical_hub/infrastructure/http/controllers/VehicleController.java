package com.fiap.mechanical_hub.infrastructure.http.controllers;

import com.fiap.mechanical_hub.application.dto.vehicle.InsertVehicleRequest;
import com.fiap.mechanical_hub.application.dto.vehicle.UpdateVehicleRequest;
import com.fiap.mechanical_hub.application.dto.vehicle.VehicleResponse;
import com.fiap.mechanical_hub.application.usecases.vehicle.CreateVehicleUseCase;
import com.fiap.mechanical_hub.application.usecases.vehicle.DeleteVehicleUseCase;
import com.fiap.mechanical_hub.application.usecases.vehicle.FindAllVehiclesUseCase;
import com.fiap.mechanical_hub.application.usecases.vehicle.FindVehicleByIdUseCase;
import com.fiap.mechanical_hub.application.usecases.vehicle.UpdateVehicleUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.fiap.mechanical_hub.application.mappers.VehicleMapper.toCreateVehicleCommand;
import static com.fiap.mechanical_hub.application.mappers.VehicleMapper.toUpdateVehicleCommand;

@RestController
@RequestMapping("/vehicles")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Veículos", description = "Endpoints para gerenciamento de veículos de clientes")
public class VehicleController {

	private final CreateVehicleUseCase createVehicleUseCase;
	private final FindVehicleByIdUseCase findVehicleByIdUseCase;
	private final FindAllVehiclesUseCase findAllVehiclesUseCase;
	private final UpdateVehicleUseCase updateVehicleUseCase;
	private final DeleteVehicleUseCase deleteVehicleUseCase;

	@PostMapping
	@Operation(summary = "Criar novo veículo", description = "Registra um novo veículo para um cliente. Requer perfil de Administrador.")
	@SecurityRequirement(name = "bearerAuth")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Veículo criado com sucesso"),
			@ApiResponse(responseCode = "400", description = "Dados inválidos"),
			@ApiResponse(responseCode = "401", description = "Não autenticado"),
			@ApiResponse(responseCode = "403", description = "Sem permissão (requer Administrador)"),
			@ApiResponse(responseCode = "404", description = "Cliente não encontrado")
	})
	public ResponseEntity<VehicleResponse> create(
			@RequestParam("customer_id") UUID customerId, @RequestBody InsertVehicleRequest request
	) {
		var command = toCreateVehicleCommand(request, customerId);
		VehicleResponse response = createVehicleUseCase.execute(command);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping
	@Operation(summary = "Listar todos os veículos", description = "Retorna lista de todos os veículos cadastrados. Requer autenticação.")
	@SecurityRequirement(name = "bearerAuth")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Lista de veículos"),
			@ApiResponse(responseCode = "401", description = "Não autenticado"),
			@ApiResponse(responseCode = "403", description = "Sem permissão (requer Administrador)")
	})
	public ResponseEntity<List<VehicleResponse>> findAll() {
		List<VehicleResponse> vehicles = findAllVehiclesUseCase.execute();
		return ResponseEntity.ok(vehicles);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Obter veículo por ID", description = "Retorna os detalhes de um veículo específico. Requer autenticação.")
	@SecurityRequirement(name = "bearerAuth")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Veículo encontrado"),
			@ApiResponse(responseCode = "401", description = "Não autenticado"),
			@ApiResponse(responseCode = "403", description = "Sem permissão (requer Administrador)"),
			@ApiResponse(responseCode = "404", description = "Veículo não encontrado")
	})
	public ResponseEntity<VehicleResponse> findById(@PathVariable UUID id) {
		VehicleResponse vehicle = findVehicleByIdUseCase.execute(id);
		return ResponseEntity.ok(vehicle);
	}

	@PutMapping("/{id}")
	@Operation(summary = "Atualizar veículo", description = "Atualiza os dados de um veículo. Requer perfil de Administrador.")
	@SecurityRequirement(name = "bearerAuth")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Veículo atualizado com sucesso"),
			@ApiResponse(responseCode = "400", description = "Dados inválidos"),
			@ApiResponse(responseCode = "401", description = "Não autenticado"),
			@ApiResponse(responseCode = "403", description = "Sem permissão (requer Administrador)"),
			@ApiResponse(responseCode = "404", description = "Veículo não encontrado")
	})
	public ResponseEntity<VehicleResponse> update(@PathVariable UUID id, @RequestBody UpdateVehicleRequest request) {
		var command = toUpdateVehicleCommand(id, request);
		VehicleResponse response = updateVehicleUseCase.execute(command);
		return ResponseEntity.ok(response);
	}

}
