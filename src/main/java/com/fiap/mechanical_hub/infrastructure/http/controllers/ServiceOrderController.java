package com.fiap.mechanical_hub.infrastructure.http.controllers;

import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.domain.enums.OrderStatusEnum;
import com.fiap.mechanical_hub.domain.enums.TaskStatusEnum;
import com.fiap.mechanical_hub.infrastructure.security.UserSecurityAdapter;
import com.fiap.mechanical_hub.application.dto.serviceorder.*;
import com.fiap.mechanical_hub.application.usecases.ServiceOrderUseCase;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/service-orders")
@RequiredArgsConstructor
@Slf4j
public class ServiceOrderController {

    private final ServiceOrderUseCase useCase;

    @PostMapping
    public ResponseEntity<ServiceOrderResponse> create(
            @RequestBody CreateServiceOrderRequest request,
            @AuthenticationPrincipal UserSecurityAdapter userDetails) {
        UUID createdByUserId = userDetails.user().getId();
        ServiceOrderResponse response = useCase.create(request, createdByUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ServiceOrder> updateStatus(
            @PathVariable UUID id, @RequestBody UpdateStatusRequest request) {
        ServiceOrder response = useCase.updateOrderStatus(
                id, OrderStatusEnum.fromString(request.getStatus()));
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ServiceOrderSummaryResponse>> findAll() {
        return ResponseEntity.ok(useCase.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceOrderDetailResponse> findById(@PathVariable UUID id) {
        ServiceOrderDetailResponse response = useCase.findById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/services")
    public ResponseEntity<Void> addServices(
            @PathVariable("id") UUID serviceOrderId, @Valid @RequestBody AddServicesToOrderRequest request) {
        useCase.addServices(serviceOrderId, request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/services/{taskId}/status")
    public ResponseEntity<Void> updateTaskStatus(@PathVariable UUID id, @PathVariable UUID taskId,
                                                 @RequestBody UpdateStatusRequest request){
        useCase.updateTaskStatus(id, taskId, TaskStatusEnum.fromString(request.getStatus()));
        return ResponseEntity.noContent().build();
    }

}