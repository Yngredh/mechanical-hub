package com.fiap.mechanical_hub.infrastructure.http.mappers;

import com.fiap.mechanical_hub.application.dto.serviceorder.OrderTaskResponse;
import com.fiap.mechanical_hub.domain.entities.OrderTask;

public class OrderTaskHttpMapper {

    public static OrderTaskResponse toTaskResponse(OrderTask task) {
        return new OrderTaskResponse(
                task.getId(),
                task.getServiceOrderId(),
                ServiceHttpMapper.toResponse(task.getServiceData()),
                task.getStatus().getDisplayName(),
                task.getStartedAt(),
                task.getFinishedAt()
        );
    }
}
