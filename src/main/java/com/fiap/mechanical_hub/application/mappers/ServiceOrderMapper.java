package com.fiap.mechanical_hub.application.mappers;

import com.fiap.mechanical_hub.application.dto.customer.CustomerResponse;
import com.fiap.mechanical_hub.application.dto.serviceorder.OrderTaskResponse;
import com.fiap.mechanical_hub.application.dto.serviceorder.ServiceOrderDetailResponse;
import com.fiap.mechanical_hub.application.dto.serviceorder.ServiceOrderResponse;
import com.fiap.mechanical_hub.application.dto.serviceorder.ServiceOrderSummaryResponse;
import com.fiap.mechanical_hub.application.dto.vehicle.VehicleResponse;
import com.fiap.mechanical_hub.domain.entities.OrderTask;
import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
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

    public ServiceOrderDetailResponse toDetailResponse(ServiceOrder order, CustomerResponse customer, VehicleResponse vehicle) {

        List<OrderTaskResponse> orderTasks = order.getOrderTasks() != null
                ? order.getOrderTasks().stream().map(this::toTaskResponse).toList()
                : List.of();

        return new ServiceOrderDetailResponse(
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
                customer,
                vehicle,
                orderTasks
        );
    }

    public ServiceOrderSummaryResponse toSummaryResponse(ServiceOrder order, CustomerResponse customer, VehicleResponse vehicle) {

        return new ServiceOrderSummaryResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getStatus().getDisplayName(),
                customer,
                vehicle,
                order.getBudget(),
                order.getCreatedAt()
        );
    }

    public Optional<ServiceOrderSummaryResponse> toSummaryResponseOptional(ServiceOrder order, CustomerResponse customer, VehicleResponse vehicle) {

        var summary = new ServiceOrderSummaryResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getStatus().getDisplayName(),
                customer,
                vehicle,
                order.getBudget(),
                order.getCreatedAt()
        );

        return Optional.of(summary);

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