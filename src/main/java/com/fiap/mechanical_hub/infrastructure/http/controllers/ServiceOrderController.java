package com.fiap.mechanical_hub.infrastructure.http.controllers;

import com.fiap.mechanical_hub.application.dto.serviceorder.*;
import com.fiap.mechanical_hub.application.usecases.ServiceOrderService;
import com.fiap.mechanical_hub.application.usecases.ServiceOrderStatusUseCase;
import com.fiap.mechanical_hub.infrastructure.http.middlewares.RequireProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/service-orders")
@RequiredArgsConstructor
public class ServiceOrderController {

    private final ServiceOrderService serviceOrderService;
    private final ServiceOrderStatusUseCase serviceOrderStatusUseCase;

    @PostMapping
    @RequireProfile("Administrador")
    public ResponseEntity<ServiceOrderResponse> create(@RequestBody CreateServiceOrderRequest request) {
        ServiceOrderResponse response = serviceOrderService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}/status")
    // TODO: Add @PreAuthorize when Spring Security is configured
    // @PreAuthorize("hasRole('MECHANICAL') or hasRole('MANAGER') or hasRole('ADMINISTRATOR')")
    public ResponseEntity<ServiceOrderResponse> updateStatus(
            @PathVariable UUID id,
            @RequestBody UpdateOrderStatusRequest request) {

        ServiceOrderResponse response = serviceOrderStatusUseCase.updateStatus(id, request.getStatus(), request.getResponsibleUserId());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @RequireProfile("Administrador")
    public ResponseEntity<List<ServiceOrderSummaryResponse>> findAll(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) UUID customerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<ServiceOrderSummaryResponse> response = serviceOrderStatusUseCase.findAll(status, customerId, startDate, endDate);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @RequireProfile("Administrador")
    public ResponseEntity<ServiceOrderDetailResponse> findById(@PathVariable UUID id) {
        ServiceOrderDetailResponse response = serviceOrderStatusUseCase.findById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<ServiceOrderResponse> approve(@PathVariable UUID id) {
        ServiceOrderResponse response = serviceOrderStatusUseCase.approve(id);
        return ResponseEntity.ok(response);
    }
}