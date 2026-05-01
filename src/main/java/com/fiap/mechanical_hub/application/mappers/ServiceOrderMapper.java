package com.fiap.mechanical_hub.application.mappers;

import com.fiap.mechanical_hub.application.dto.customer.CustomerResponse;
import com.fiap.mechanical_hub.application.dto.serviceorder.ServiceOrderDetailResponse;
import com.fiap.mechanical_hub.application.dto.serviceorder.ServiceOrderResponse;
import com.fiap.mechanical_hub.application.dto.serviceorder.ServiceOrderSummaryResponse;
import com.fiap.mechanical_hub.application.dto.serviceorder.request.ServiceOrderCustomerView;
import com.fiap.mechanical_hub.application.dto.vehicle.VehicleResponse;
import com.fiap.mechanical_hub.domain.entities.*;
import com.fiap.mechanical_hub.infrastructure.database.models.ServiceOrderModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ServiceOrderMapper {

    public ServiceOrderResponse toResponse(ServiceOrder order) {

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
                order.getOrderTasks().stream().map(OrderTaskMapper::toTaskResponse).toList()
        );
    }
    public static ServiceOrderDetailResponse toDetailResponse(
            ServiceOrder serviceOrder,
            VehicleResponse vehicle,
            CustomerResponse customer,
            List<ServiceData> serviceData
    ) {
        return new ServiceOrderDetailResponse(
                serviceOrder.getId(),
                serviceOrder.getOrderNumber(),
                customer,
                vehicle,
                serviceOrder.getStatus().name(),
                serviceOrder.getRequestDescription(),
                serviceOrder.getBudget(),
                serviceOrder.isHasStockPending(),
                serviceData,
                serviceOrder.getOrderTasks(),
                serviceOrder.getCreatedAt()
        );
    }

    public static ServiceOrderSummaryResponse toSummaryResponse(ServiceOrder order) {
        return new ServiceOrderSummaryResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getStatus().getDisplayName(),
                order.isHasStockPending(),
                order.getCreatedAt()
        );
    }

    public static ServiceOrderCustomerView toCustomerView(
            ServiceOrder order, VehicleResponse vehicle, CustomerResponse customer, List<String> services) {
        return new ServiceOrderCustomerView(
                order.getOrderNumber(),
                customer.getName(),
                vehicle.getLicensePlate(),
                vehicle.getModel(),
                vehicle.getBrand(),
                order.getStatus(),
                order.getBudget(),
                services,
                order.getOpenedAt(),
                order.getCompletedAt(),
                order.getDeliveredAt()
        );
    }

    public static ServiceOrderModel toJpaEntity(ServiceOrder order) {
        ServiceOrderModel model = new ServiceOrderModel();

        model.setId(order.getId());
        model.setVehicleId(order.getVehicleId());
        model.setCustomerId(order.getCustomerId());
        model.setOrderStatusEnum(order.getStatus());
        model.setCreatedByUserId(order.getCreatedByUserId());
        model.setResponsibleUserId(order.getResponsibleUserId());
        model.setOrderNumber(order.getOrderNumber());
        model.setRequestDescription(order.getRequestDescription());
        model.setBudget(order.getBudget());
        model.setHasStockPending(order.isHasStockPending());
        model.setEstimatedCompletionAt(order.getEstimatedCompletionAt());
        model.setOpenedAt(order.getOpenedAt());
        model.setCompletedAt(order.getCompletedAt());
        model.setDeliveredAt(order.getDeliveredAt());
        model.setCreatedAt(order.getCreatedAt());
        model.setUpdatedAt(order.getUpdatedAt());
        order.getOrderTasks()
                .stream()
                .map(task -> OrderTaskMapper.toJpaEntity(task, model))
                .forEach(model::addOrderTask);
        return model;
    }

    public static ServiceOrder toDomainEntity(ServiceOrderModel entity) {
        return new ServiceOrder(
                entity.getId(),
                entity.getVehicleId(),
                entity.getCustomerId(),
                entity.getOrderStatusEnum(),
                entity.getCreatedByUserId(),
                entity.getResponsibleUserId(),
                entity.getOrderNumber(),
                entity.getRequestDescription(),
                entity.getBudget(),
                entity.isHasStockPending(),
                entity.getEstimatedCompletionAt(),
                entity.getOpenedAt(),
                entity.getCompletedAt(),
                entity.getDeliveredAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getOrderTasks()
                        .stream().map(OrderTaskMapper::toDomainEntity)
                        .collect(Collectors.toCollection(ArrayList::new))
        );
    }
}