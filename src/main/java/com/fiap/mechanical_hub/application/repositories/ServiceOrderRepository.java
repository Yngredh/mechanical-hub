package com.fiap.mechanical_hub.application.repositories;

import com.fiap.mechanical_hub.domain.entities.ServiceOrder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServiceOrderRepository {
    Optional<ServiceOrder> findById(UUID id);
    ServiceOrder save(ServiceOrder order);
    List<ServiceOrder> findAll();
    void deleteById(UUID id);
}
