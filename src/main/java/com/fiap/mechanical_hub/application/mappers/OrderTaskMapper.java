package com.fiap.mechanical_hub.application.mappers;

import com.fiap.mechanical_hub.application.dto.serviceorder.OrderTaskResponse;
import com.fiap.mechanical_hub.domain.entities.OrderTask;
import com.fiap.mechanical_hub.domain.enums.TaskStatusEnum;
import com.fiap.mechanical_hub.infrastructure.database.models.OrderTaskModel;
import com.fiap.mechanical_hub.infrastructure.database.models.ServiceOrderModel;
import org.springframework.stereotype.Component;

@Component
public class OrderTaskMapper {

    private OrderTaskMapper() {}

    public static OrderTaskModel toJpaEntity(OrderTask task, ServiceOrderModel parent) {
        return new OrderTaskModel(
                task.getId(),
                parent,
                ServiceMapper.toJpaEntity(task.getServiceData()),
                task.getStatus().name(),
                task.getStartedAt(),
                task.getFinishedAt()
        );
    }

    public static OrderTask toDomainEntity(OrderTaskModel entity) {
        return new OrderTask(
                entity.getId(),
                entity.getServiceOrder().getId(),
                ServiceMapper.toDomainEntity(entity.getService()),
                TaskStatusEnum.valueOf(entity.getServiceStatus()),
                entity.getStartedAt(),
                entity.getFinishedAt()
        );
    }

    public static OrderTaskResponse toTaskResponse(OrderTask task) {
        return new OrderTaskResponse(
                task.getId(),
                task.getServiceOrderId(),
                ServiceMapper.toResponse(task.getServiceData()),
                task.getStatus().getDisplayName(),
                task.getStartedAt(),
                task.getFinishedAt()
        );
    }
}
