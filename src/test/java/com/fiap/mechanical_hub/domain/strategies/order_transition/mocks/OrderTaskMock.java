package com.fiap.mechanical_hub.domain.strategies.order_transition.mocks;

import com.fiap.mechanical_hub.domain.entities.OrderTask;
import com.fiap.mechanical_hub.domain.entities.ServiceData;

import java.util.UUID;

public final class OrderTaskMock {

    private OrderTaskMock() {
    }

    public static OrderTask pendingTask(UUID serviceOrderId, UUID serviceId) {
        ServiceData service = ServiceMock.serviceWithId(serviceId);
        return OrderTask.create(serviceOrderId, service);
    }

    public static OrderTask finishedTask(UUID serviceOrderId, UUID serviceId) {
        ServiceData service = ServiceMock.serviceWithId(serviceId);

        OrderTask task = OrderTask.create(serviceOrderId, service);
        task.start();
        task.finish();
        return task;
    }
}

