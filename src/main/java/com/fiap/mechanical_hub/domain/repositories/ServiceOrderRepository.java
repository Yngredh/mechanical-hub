package com.fiap.mechanical_hub.domain.repositories;

import com.fiap.mechanical_hub.domain.entities.ServiceOrder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServiceOrderRepository {

    ServiceOrder save(ServiceOrder serviceOrder);

    Optional<ServiceOrder> findById(UUID id);

    List<ServiceOrder> findAll();

    Optional<String> findLastOrderNumberByYearMonth(String yearMonth);
}