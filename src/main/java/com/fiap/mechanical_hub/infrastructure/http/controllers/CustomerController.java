package com.fiap.mechanical_hub.infrastructure.http.controllers;

import com.fiap.mechanical_hub.application.dto.customer.UpsertCustomerRequest;
import com.fiap.mechanical_hub.application.dto.customer.CustomerResponse;
import com.fiap.mechanical_hub.application.dto.serviceorder.ServiceOrderSummaryResponse;
import com.fiap.mechanical_hub.application.usecases.CustomerService;
import com.fiap.mechanical_hub.application.usecases.ServiceOrderStatusUseCase;
import com.fiap.mechanical_hub.infrastructure.http.middlewares.RequireProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final ServiceOrderStatusUseCase serviceOrderStatusUseCase;

    @PostMapping
    @RequireProfile("Administrador")
    public ResponseEntity<CustomerResponse> create(@RequestBody UpsertCustomerRequest request) {
        CustomerResponse response = customerService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @RequireProfile("Administrador")
    public ResponseEntity<List<CustomerResponse>> findAll() {
        List<CustomerResponse> customers = customerService.findAll();
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/{id}")
    @RequireProfile("Administrador")
    public ResponseEntity<CustomerResponse> findById(@PathVariable UUID id) {
        CustomerResponse customer = customerService.findById(id);
        return ResponseEntity.ok(customer);
    }

    @GetMapping("/{id}/orders")
    @RequireProfile("Administrador")
    public ResponseEntity<List<ServiceOrderSummaryResponse>> findOrdersByCustomerId(@PathVariable UUID id) {
        customerService.findById(id);
        List<ServiceOrderSummaryResponse> orders = serviceOrderStatusUseCase.findByCustomerId(id);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{id}")
    @RequireProfile("Administrador")
    public ResponseEntity<CustomerResponse> update(@PathVariable UUID id, @RequestBody UpsertCustomerRequest request) {
        CustomerResponse response = customerService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @RequireProfile("Administrador")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        customerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
