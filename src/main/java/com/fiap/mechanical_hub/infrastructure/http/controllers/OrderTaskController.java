package com.fiap.mechanical_hub.infrastructure.http.controllers;

import com.fiap.mechanical_hub.application.dto.ordertask.OrderTaskStatusUpdateRequest;
import com.fiap.mechanical_hub.application.dto.serviceorder.OrderTaskResponse;
import com.fiap.mechanical_hub.application.mappers.OrderTaskMapper;
import com.fiap.mechanical_hub.domain.entities.OrderTask;
import com.fiap.mechanical_hub.domain.services.OrderTaskDomainService;
import com.fiap.mechanical_hub.infrastructure.http.middlewares.RequireProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/order-services")
@RequiredArgsConstructor
public class OrderTaskController {

    private final OrderTaskDomainService orderTaskDomainService;
    private final OrderTaskMapper orderTaskMapper;

    @PatchMapping("/{id}/status")
    @RequireProfile("Mecânico")
    public ResponseEntity<OrderTaskResponse> updateStatus(
            @PathVariable UUID id,
            @RequestBody OrderTaskStatusUpdateRequest request) {

        OrderTask updatedTask = orderTaskDomainService.updateStatus(id, request.getStatus());
        OrderTaskResponse response = orderTaskMapper.toResponse(updatedTask);

        return ResponseEntity.ok(response);
    }
}
