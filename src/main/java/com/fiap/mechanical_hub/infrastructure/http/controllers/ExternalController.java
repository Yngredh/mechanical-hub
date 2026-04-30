package com.fiap.mechanical_hub.infrastructure.http.controllers;

import com.fiap.mechanical_hub.application.dto.serviceorder.request.ServiceOrderCustomerView;
import com.fiap.mechanical_hub.application.usecases.ServiceOrderUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/mechanical-hub")
@RequiredArgsConstructor
public class ExternalController {

    private final ServiceOrderUseCase useCase;

    @PostMapping("/service-orders/{id}/approve")
    public ResponseEntity<Void> approve(@PathVariable UUID id) {
        useCase.approve(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/service-orders/{id}/reject")
    public ResponseEntity<Void> reject(@PathVariable UUID id) {
        useCase.reject(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/service-orders/{orderNumber}")
    public ResponseEntity<ServiceOrderCustomerView> findByOrderNumber(@PathVariable String orderNumber) {
        var response = useCase.findByOrderNumber(orderNumber);
        return ResponseEntity.ok(response);
    }
}