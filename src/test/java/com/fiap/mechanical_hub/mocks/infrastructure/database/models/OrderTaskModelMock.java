package com.fiap.mechanical_hub.mocks.infrastructure.database.models;

import com.fiap.mechanical_hub.domain.enums.TaskStatusEnum;
import com.fiap.mechanical_hub.infrastructure.database.models.OrderTaskModel;
import com.fiap.mechanical_hub.infrastructure.database.models.ServiceOrderModel;

import java.time.LocalDateTime;
import java.util.UUID;

public class OrderTaskModelMock {

    public static final UUID TASK_ID = UUID.fromString("00000000-0000-0000-0000-000000000010");
    public static final UUID SERVICE_ORDER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    public static OrderTaskModel notStarted() {
        ServiceOrderModel serviceOrderRef = new ServiceOrderModel();
        serviceOrderRef.setId(SERVICE_ORDER_ID);

        OrderTaskModel model = new OrderTaskModel();
        model.setId(TASK_ID);
        model.setServiceOrder(serviceOrderRef);
        model.setService(ServiceModelMock.withNoMaterials());
        model.setServiceStatus(TaskStatusEnum.PENDENTE.name());
        model.setStartedAt(null);
        model.setFinishedAt(null);
        return model;
    }

    public static OrderTaskModel finished() {
        LocalDateTime startedAt = LocalDateTime.of(2024, 1, 1, 8, 0);
        LocalDateTime finishedAt = LocalDateTime.of(2024, 1, 1, 10, 0);

        OrderTaskModel model = notStarted();
        model.setServiceStatus(TaskStatusEnum.FINALIZADO.name());
        model.setStartedAt(startedAt);
        model.setFinishedAt(finishedAt);
        return model;
    }
}
