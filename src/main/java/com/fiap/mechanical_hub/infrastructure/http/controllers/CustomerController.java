package com.fiap.mechanical_hub.infrastructure.http.controllers;

import com.fiap.mechanical_hub.application.dto.customer.CustomerResponse;
import com.fiap.mechanical_hub.application.dto.customer.UpsertCustomerRequest;
import com.fiap.mechanical_hub.application.dto.serviceorder.ServiceOrderSummaryResponse;
import com.fiap.mechanical_hub.application.usecases.CustomerUseCase;
import com.fiap.mechanical_hub.application.usecases.ServiceOrderStatusUseCase;
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

    private final CustomerUseCase customerUseCase;
    private final ServiceOrderStatusUseCase serviceOrderStatusUseCase;

    @PostMapping
    public ResponseEntity<CustomerResponse> create(@RequestBody UpsertCustomerRequest request) {
        CustomerResponse response = customerUseCase.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> findAll() {
        List<CustomerResponse> customers = customerUseCase.findAll();
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> findById(@PathVariable UUID id) {
        CustomerResponse customer = customerUseCase.findById(id);
        return ResponseEntity.ok(customer);
    }

    @GetMapping("/{id}/orders")
    public ResponseEntity<List<ServiceOrderSummaryResponse>> findOrdersByCustomerId(@PathVariable UUID id) {
        List<ServiceOrderSummaryResponse> orders = serviceOrderStatusUseCase.findByCustomerId(id);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> update(@PathVariable UUID id, @RequestBody UpsertCustomerRequest request) {
        CustomerResponse response = customerUseCase.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        customerUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}
