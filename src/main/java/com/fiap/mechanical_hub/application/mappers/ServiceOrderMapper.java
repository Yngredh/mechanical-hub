package com.fiap.mechanical_hub.application.mappers;

import com.fiap.mechanical_hub.application.dto.serviceorder.ServiceOrderResponse;
import com.fiap.mechanical_hub.application.dto.serviceorder.OrderTaskResponse;
import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.domain.entities.OrderTask;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ServiceOrderMapper {

    public ServiceOrderResponse toResponse(ServiceOrder order) {
        List<OrderTaskResponse> orderTasks = order.getOrderTasks() != null 
            ? order.getOrderTasks().stream()
                .map(this::toTaskResponse)
                .collect(Collectors.toList())
            : List.of();

        return new ServiceOrderResponse(
                order.getId(),
                order.getVehicleId(),
                order.getCustomerId(),
                order.getStatus().getDisplayName(),
                order.getCreatedByUserId(),
                order.getResponsibleUserId(),
                order.getOrderNumber(),
                order.getRequestDescription(),
                order.getBudget(),
                order.isHasStockPending(),
                order.getEstimatedCompletionAt(),
                order.getOpenedAt(),
                order.getCompletedAt(),
                order.getDeliveredAt(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                orderTasks
        );
    }

    public OrderTaskResponse toTaskResponse(OrderTask task) {
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

