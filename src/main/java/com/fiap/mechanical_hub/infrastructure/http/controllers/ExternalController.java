package com.fiap.mechanical_hub.infrastructure.http.controllers;

import com.fiap.mechanical_hub.application.usecases.ServiceOrderUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/mechanical-hub/service-orders")
@RequiredArgsConstructor
public class ExternalController {

    private final ServiceOrderUseCase useCase;

    @PostMapping("/{id}/approve")
    public ResponseEntity<Void> approve(@PathVariable UUID id) {
        useCase.approve(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<Void> reject(@PathVariable UUID id) {
        useCase.approve(id);
        return ResponseEntity.noContent().build();
    }

    // TODO findOSbyCustomerID
}