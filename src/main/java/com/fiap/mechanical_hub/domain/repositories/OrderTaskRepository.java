package com.fiap.mechanical_hub.domain.repositories;

import com.fiap.mechanical_hub.domain.entities.OrderTask;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderTaskRepository {

    Optional<OrderTask> findById(UUID id);

    List<OrderTask> findAll();

    List<Object[]> findAverageExecutionTimeByService();

    List<OrderTask> findAllByServiceId(UUID serviceId);
}

