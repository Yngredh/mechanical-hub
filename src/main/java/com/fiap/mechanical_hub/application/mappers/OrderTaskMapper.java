package com.fiap.mechanical_hub.application.mappers;

import com.fiap.mechanical_hub.application.dto.serviceorder.OrderTaskResponse;
import com.fiap.mechanical_hub.domain.entities.OrderTask;
import org.springframework.stereotype.Component;

@Component
public class OrderTaskMapper {

    public OrderTaskResponse toResponse(OrderTask task) {
        return new OrderTaskResponse(
                task.getId(),
                task.getServiceOrderId(),
                task.getServiceId(),
                task.getStatus().getDisplayName(),
                task.getStartedAt(),
                task.getFinishedAt()
        );
    }
}
