package com.fiap.mechanical_hub.infrastructure.database.mappers;

import com.fiap.mechanical_hub.domain.entities.OrderTask;
import com.fiap.mechanical_hub.domain.entities.ServiceData;
import com.fiap.mechanical_hub.domain.entities.ServiceMaterial;
import com.fiap.mechanical_hub.domain.enums.TaskStatusEnum;
import com.fiap.mechanical_hub.infrastructure.database.models.OrderTaskModel;
import com.fiap.mechanical_hub.infrastructure.database.models.ServiceModel;
import com.fiap.mechanical_hub.infrastructure.database.models.ServiceMaterialModel;
import com.fiap.mechanical_hub.infrastructure.database.models.ServiceOrderModel;

public class OrderTaskRepositoryMapper {

    private OrderTaskRepositoryMapper() {}

    public static OrderTask toDomainEntity(OrderTaskModel entity) {
        return new OrderTask(
                entity.getId(),
                entity.getServiceOrder().getId(),
                ServiceRepositoryMapper.toDomainEntity(entity.getService()),
                TaskStatusEnum.valueOf(entity.getServiceStatus()),
                entity.getStartedAt(),
                entity.getFinishedAt()
        );
    }

    public static OrderTaskModel toJpaEntity(OrderTask task, ServiceOrderModel parent) {
        return new OrderTaskModel(
                task.getId(),
                parent,
                OrderTaskRepositoryMapper.toModel(task.getServiceData()),
                task.getStatus().name(),
                task.getStartedAt(),
                task.getFinishedAt()
        );
    }

    public static ServiceModel toModel(ServiceData serviceData) {
        ServiceModel model = new ServiceModel();

        model.setId(serviceData.getId());
        model.setName(serviceData.getName());
        model.setDescription(serviceData.getDescription());
        model.setLaborCost(serviceData.getLaborCost());
        model.setBasePrice(serviceData.getBasePrice());
        model.setTotalPrice(serviceData.getTotalPrice());
        model.setCreatedAt(serviceData.getCreatedAt());
        model.setUpdatedAt(serviceData.getUpdatedAt());
        model.setActive(serviceData.isActive());

        for (ServiceMaterial item : serviceData.getMaterials()) {
            ServiceMaterialModel child = new ServiceMaterialModel();
            child.setId(item.getId());
            child.setMaterial(MaterialRepositoryMapper.toModel(item.getMaterial()));
            child.setQuantity(item.getQuantity());
            child.setService(model);
            model.getMaterials().add(child);
        }

        return model;
    }
}

