package com.fiap.mechanical_hub.infrastructure.http.controllers;

import com.fiap.mechanical_hub.application.dto.material.UpsertMaterialRequest;
import com.fiap.mechanical_hub.application.dto.material.MaterialResponse;
import com.fiap.mechanical_hub.application.usecases.MaterialUseCase;
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
public class MaterialController {

    private final MaterialUseCase materialUseCase;

    @PostMapping
    public ResponseEntity<MaterialResponse> create(@Valid @RequestBody UpsertMaterialRequest request) {
        MaterialResponse response = materialUseCase.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<MaterialResponse>> findAll() {
        List<MaterialResponse> materials = materialUseCase.findAll();
        return ResponseEntity.ok(materials);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaterialResponse> findById(@PathVariable UUID id) {
        MaterialResponse material = materialUseCase.findMaterialById(id);
        return ResponseEntity.ok(material);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MaterialResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpsertMaterialRequest request) {
        MaterialResponse response = materialUseCase.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        materialUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}

