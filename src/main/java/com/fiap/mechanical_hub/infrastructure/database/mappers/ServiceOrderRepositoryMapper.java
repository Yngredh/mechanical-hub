package com.fiap.mechanical_hub.infrastructure.database.mappers;

import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.infrastructure.database.models.ServiceOrderModel;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class ServiceOrderRepositoryMapper {

    private ServiceOrderRepositoryMapper() {
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
                .map(task -> OrderTaskRepositoryMapper.toJpaEntity(task, model))
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
                        .stream().map(OrderTaskRepositoryMapper::toDomainEntity)
                        .collect(Collectors.toCollection(ArrayList::new))
        );
    }
}
