package com.fiap.mechanical_hub.domain.strategies.order_transition;

import com.fiap.mechanical_hub.domain.entities.ServiceOrder;

public interface OrderStatusTransition {
    void execute(ServiceOrder order);
}
