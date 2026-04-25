package com.fiap.mechanical_hub.infrastructure.database.repositories.adapter;

import com.fiap.mechanical_hub.domain.entities.OrderTask;
import com.fiap.mechanical_hub.domain.enums.TaskStatus;
import com.fiap.mechanical_hub.application.repositories.OrderTaskRepository;
import com.fiap.mechanical_hub.infrastructure.database.models.OrderTaskModel;
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
        return jpaRepository.findById(id).map(this::toDomainEntity);
    }

    @Override
    public OrderTask save(OrderTask task) {
        OrderTaskModel entity = toJpaEntity(task);
        OrderTaskModel saved = jpaRepository.save(entity);
        return toDomainEntity(saved);
    }

    @Override
    public List<OrderTask> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomainEntity)
                .toList();
    }

    @Override
    public List<OrderTask> findByServiceOrderId(UUID serviceOrderId) {
        return jpaRepository.findByServiceOrderId(serviceOrderId).stream()
                .map(this::toDomainEntity)
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

    private OrderTaskModel toJpaEntity(OrderTask task) {
        return new OrderTaskModel(
                task.getId(),
                task.getServiceOrderId(),
                task.getServiceId(),
                task.getStatus().name(),
                task.getStartedAt(),
                task.getFinishedAt()
        );
    }

    private OrderTask toDomainEntity(OrderTaskModel entity) {
        return new OrderTask(
                entity.getId(),
                entity.getServiceOrderId(),
                entity.getServiceId(),
                TaskStatus.valueOf(entity.getServiceStatus()),
                entity.getStartedAt(),
                entity.getFinishedAt()
        );
    }
}


