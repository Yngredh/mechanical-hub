package com.fiap.mechanical_hub.domain.repositories;

import com.fiap.mechanical_hub.domain.entities.ServiceOrder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServiceOrderRepository {
    Optional<ServiceOrder> findById(UUID id);
    ServiceOrder save(ServiceOrder order);
    List<ServiceOrder> findAll();
    List<ServiceOrder> findAllFiltered(String status, UUID customerId, LocalDateTime startDate, LocalDateTime endDate);
    void deleteById(UUID id);
}
