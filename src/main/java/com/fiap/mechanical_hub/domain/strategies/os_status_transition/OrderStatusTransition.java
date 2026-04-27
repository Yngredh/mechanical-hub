package com.fiap.mechanical_hub.domain.strategies.os_status_transition;

import com.fiap.mechanical_hub.domain.entities.ServiceOrder;

public interface OrderStatusTransition {
    void execute(ServiceOrder order);
}
