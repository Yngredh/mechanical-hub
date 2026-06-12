package com.fiap.mechanical_hub.mocks.infrastructure.database.models;

import com.fiap.mechanical_hub.domain.enums.OrderStatusEnum;
import com.fiap.mechanical_hub.infrastructure.database.models.OrderTaskModel;
import com.fiap.mechanical_hub.infrastructure.database.models.ServiceOrderModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class ServiceOrderModelMock {

    public static final UUID ORDER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    public static final UUID VEHICLE_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");
    public static final UUID CUSTOMER_ID = UUID.fromString("00000000-0000-0000-0000-000000000003");
    public static final UUID USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000004");
    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2024, 1, 1, 10, 0);
    private static final LocalDateTime UPDATED_AT = LocalDateTime.of(2024, 1, 2, 10, 0);

    public static ServiceOrderModel received() {
        ServiceOrderModel model = new ServiceOrderModel();
        model.setId(ORDER_ID);
        model.setVehicleId(VEHICLE_ID);
        model.setCustomerId(CUSTOMER_ID);
        model.setOrderStatusEnum(OrderStatusEnum.RECEBIDO);
        model.setCreatedByUserId(USER_ID);
        model.setResponsibleUserId(null);
        model.setOrderNumber("OS-202401-0001");
        model.setRequestDescription("Diagnóstico e reparo");
        model.setBudget(BigDecimal.valueOf(500.00));
        model.setHasStockPending(false);
        model.setEstimatedCompletionAt(null);
        model.setOpenedAt(null);
        model.setCompletedAt(null);
        model.setDeliveredAt(null);
        model.setCreatedAt(CREATED_AT);
        model.setUpdatedAt(UPDATED_AT);
        return model;
    }

    public static ServiceOrderModel withOneTask() {
        ServiceOrderModel model = received();

        OrderTaskModel task = OrderTaskModelMock.notStarted();
        task.setServiceOrder(model);
        model.getOrderTasks().add(task);

        return model;
    }
}
