package com.fiap.mechanical_hub.infrastructure.http.controllers;

import com.fiap.mechanical_hub.application.dto.serviceorder.ServiceOrderResponse;
import com.fiap.mechanical_hub.application.dto.serviceorder.UpdateOrderStatusRequest;
import com.fiap.mechanical_hub.application.usecases.ServiceOrderStatusUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/service-orders")
@RequiredArgsConstructor
public class ServiceOrderController {

    private final ServiceOrderStatusUseCase serviceOrderStatusUseCase;

    @PatchMapping("/{id}/status")
    // TODO: Add @PreAuthorize when Spring Security is configured
    // @PreAuthorize("hasRole('MECHANICAL') or hasRole('MANAGER') or hasRole('ADMINISTRATOR')")
    public ResponseEntity<ServiceOrderResponse> updateStatus(
            @PathVariable UUID id,
            @RequestBody UpdateOrderStatusRequest request) {

        ServiceOrderResponse response = serviceOrderStatusUseCase.updateStatus(id, request.getStatus(), request.getResponsibleUserId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceOrderResponse> findById(@PathVariable UUID id) {
        ServiceOrderResponse response = serviceOrderStatusUseCase.findById(id);
        return ResponseEntity.ok(response);
    }
}
