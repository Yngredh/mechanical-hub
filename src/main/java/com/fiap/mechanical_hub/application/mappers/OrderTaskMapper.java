package com.fiap.mechanical_hub.application.mappers;

import com.fiap.mechanical_hub.domain.entities.OrderTask;
import com.fiap.mechanical_hub.domain.enums.TaskStatusEnum;
import com.fiap.mechanical_hub.infrastructure.database.models.OrderTaskModel;
import org.springframework.stereotype.Component;

@Component
public class OrderTaskMapper {

    public static OrderTaskModel toJpaEntity(OrderTask task) {
        return new OrderTaskModel(
                task.getId(),
                task.getServiceOrderId(),
                task.getServiceId(),
                task.getStatus().name(),
                task.getStartedAt(),
                task.getFinishedAt()
        );
    }

    public static OrderTask toDomainEntity(OrderTaskModel entity) {
        return new OrderTask(
                entity.getId(),
                entity.getServiceOrderId(),
                entity.getServiceId(),
                TaskStatusEnum.valueOf(entity.getServiceStatus()),
                entity.getStartedAt(),
                entity.getFinishedAt()
        );
    }
}
