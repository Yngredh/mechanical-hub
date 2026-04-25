package com.fiap.mechanical_hub.infrastructure.http.controllers;

import com.fiap.mechanical_hub.application.dto.serviceorder.CreateServiceOrderRequest;
import com.fiap.mechanical_hub.application.dto.serviceorder.ServiceOrderResponse;
import com.fiap.mechanical_hub.application.usecases.ServiceOrderService;
import com.fiap.mechanical_hub.infrastructure.http.middlewares.RequireProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/service-orders")
@RequiredArgsConstructor
public class ServiceOrderController {

    private final ServiceOrderService serviceOrderService;

    @PostMapping
    @RequireProfile("Administrador")
    public ResponseEntity<ServiceOrderResponse> create(@RequestBody CreateServiceOrderRequest request) {
        ServiceOrderResponse response = serviceOrderService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
