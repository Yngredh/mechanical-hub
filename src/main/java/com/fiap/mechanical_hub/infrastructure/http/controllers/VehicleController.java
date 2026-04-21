package com.fiap.mechanical_hub.infrastructure.http.controllers;

import com.fiap.mechanical_hub.application.dto.vehicle.UpsertVehicleRequest;
import com.fiap.mechanical_hub.application.dto.vehicle.VehicleResponse;
import com.fiap.mechanical_hub.application.use_cases.VehicleService;
import com.fiap.mechanical_hub.infrastructure.http.middlewares.RequireProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/vehicles")
@RequiredArgsConstructor
public class VehicleController {

	private final VehicleService vehicleService;

	@PostMapping
	@RequireProfile("Administrador")
	public ResponseEntity<VehicleResponse> create(
			@RequestParam("customer_id") UUID customerId,
			@RequestBody UpsertVehicleRequest request
	) {
		VehicleResponse response = vehicleService.create(customerId, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping
	@RequireProfile("Administrador")
	public ResponseEntity<List<VehicleResponse>> findAll() {
		List<VehicleResponse> vehicles = vehicleService.findAll();
		return ResponseEntity.ok(vehicles);
	}

	@GetMapping("/{id}")
	@RequireProfile("Administrador")
	public ResponseEntity<VehicleResponse> findById(@PathVariable UUID id) {
		VehicleResponse vehicle = vehicleService.findById(id);
		return ResponseEntity.ok(vehicle);
	}

	@PutMapping("/{id}")
	@RequireProfile("Administrador")
	public ResponseEntity<VehicleResponse> update(@PathVariable UUID id, @RequestBody UpsertVehicleRequest request) {
		VehicleResponse response = vehicleService.update(id, request);
		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{id}")
	@RequireProfile("Administrador")
	public ResponseEntity<Void> delete(@PathVariable UUID id) {
		vehicleService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
