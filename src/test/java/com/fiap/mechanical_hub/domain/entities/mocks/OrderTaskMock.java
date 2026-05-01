package com.fiap.mechanical_hub.domain.entities.mocks;

import com.fiap.mechanical_hub.domain.entities.OrderTask;
import com.fiap.mechanical_hub.domain.entities.Service;

import static com.fiap.mechanical_hub.domain.entities.constants.TestConstants.*;

public class OrderTaskMock {

    public static OrderTask defaultOrderTask() {
        Service service = ServiceMock.defaultService();
        return OrderTask.create(DEFAULT_SERVICE_ORDER_ID, service);
    }

    public static OrderTask orderTaskWithCustomService(Service service) {
        return OrderTask.create(DEFAULT_SERVICE_ORDER_ID, service);
    }

    public static OrderTask orderTaskWithCustomValues(java.util.UUID serviceOrderId, Service service) {
        return OrderTask.create(serviceOrderId, service);
    }

}

