package com.fiap.mechanical_hub.infrastructure.http.controllers;

import com.fiap.mechanical_hub.application.dto.service.ServiceResponse;
import com.fiap.mechanical_hub.application.dto.service.UpsertServiceRequest;
import com.fiap.mechanical_hub.application.usecases.ServiceUseCase;
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
public class ServiceController {

    private final ServiceUseCase serviceUseCase;

    @PostMapping
    public ResponseEntity<ServiceResponse> create(
            @RequestBody @Valid UpsertServiceRequest request
    ) {
        ServiceResponse response = serviceUseCase.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ServiceResponse>> findAll() {
        List<ServiceResponse> services = serviceUseCase.findAll();
        return ResponseEntity.ok(services);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponse> findById(@PathVariable UUID id) {
        ServiceResponse response = serviceUseCase.findById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponse> update(
            @PathVariable UUID id,
            @RequestBody @Valid UpsertServiceRequest request
    ) {
        ServiceResponse response = serviceUseCase.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        serviceUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}
