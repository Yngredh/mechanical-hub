package com.fiap.mechanical_hub.infrastructure.http.controllers;

import com.fiap.mechanical_hub.application.dto.vehicle.UpsertVehicleRequest;
import com.fiap.mechanical_hub.application.dto.vehicle.VehicleResponse;
import com.fiap.mechanical_hub.application.usecases.VehicleUseCase;
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

	private final VehicleUseCase vehicleUseCase;

	@PostMapping
	public ResponseEntity<VehicleResponse> create(
			@RequestParam("customer_id") UUID customerId,
			@RequestBody UpsertVehicleRequest request
	) {
		VehicleResponse response = vehicleUseCase.create(customerId, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping
	public ResponseEntity<List<VehicleResponse>> findAll() {
		List<VehicleResponse> vehicles = vehicleUseCase.findAll();
		return ResponseEntity.ok(vehicles);
	}

	@GetMapping("/{id}")
	public ResponseEntity<VehicleResponse> findById(@PathVariable UUID id) {
		VehicleResponse vehicle = vehicleUseCase.findById(id);
		return ResponseEntity.ok(vehicle);
	}

	@PutMapping("/{id}")
	public ResponseEntity<VehicleResponse> update(@PathVariable UUID id, @RequestBody UpsertVehicleRequest request) {
		VehicleResponse response = vehicleUseCase.update(id, request);
		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable UUID id) {
		vehicleUseCase.delete(id);
		return ResponseEntity.noContent().build();
	}
}
