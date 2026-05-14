package com.fiap.mechanical_hub.infrastructure.database.repositories.adapter;

import com.fiap.mechanical_hub.application.mappers.OrderTaskMapper;
import com.fiap.mechanical_hub.domain.repositories.OrderTaskRepository;
import com.fiap.mechanical_hub.domain.entities.OrderTask;
import com.fiap.mechanical_hub.infrastructure.database.repositories.OrderTaskJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderTaskRepositoryAdapter implements OrderTaskRepository {

    private final OrderTaskJpaRepository jpaRepository;

    @Override
    public Optional<OrderTask> findById(UUID id) {
        return jpaRepository.findById(id).map(OrderTaskMapper::toDomainEntity);
    }

    @Override
    public List<OrderTask> findAll() {
        return jpaRepository.findAll().stream()
                .map(OrderTaskMapper::toDomainEntity)
                .toList();
    }

    @Override
    public List<OrderTask> findByServiceOrderId(UUID serviceOrderId) {
        return jpaRepository.findByServiceOrderId(serviceOrderId).stream()
                .map(OrderTaskMapper::toDomainEntity)
                .toList();
    }

    @Override
    public List<Object[]> findAverageExecutionTimeByService() {
        return jpaRepository.findAverageExecutionTimeByService();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

}


