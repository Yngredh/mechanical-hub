package com.fiap.mechanical_hub.infrastructure.http.controllers;

import com.fiap.mechanical_hub.application.dto.material.UpsertMaterialRequest;
import com.fiap.mechanical_hub.application.dto.material.MaterialResponse;
import com.fiap.mechanical_hub.application.services.MaterialService;
import com.fiap.mechanical_hub.infrastructure.http.middlewares.RequireProfile;
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

    private final MaterialService materialService;

    @PostMapping
    @RequireProfile("Administrador")
    public ResponseEntity<MaterialResponse> create(@Valid @RequestBody UpsertMaterialRequest request) {
        MaterialResponse response = materialService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @RequireProfile("Administrador")
    public ResponseEntity<List<MaterialResponse>> findAll() {
        List<MaterialResponse> materials = materialService.findAll();
        return ResponseEntity.ok(materials);
    }

    @GetMapping("/{id}")
    @RequireProfile("Administrador")
    public ResponseEntity<MaterialResponse> findById(@PathVariable UUID id) {
        MaterialResponse material = materialService.findById(id);
        return ResponseEntity.ok(material);
    }

    @PutMapping("/{id}")
    @RequireProfile("Administrador")
    public ResponseEntity<MaterialResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpsertMaterialRequest request) {
        MaterialResponse response = materialService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @RequireProfile("Administrador")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        materialService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

