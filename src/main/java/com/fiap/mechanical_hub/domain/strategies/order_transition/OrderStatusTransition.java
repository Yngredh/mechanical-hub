package com.fiap.mechanical_hub.domain.strategies.order_transition;

import com.fiap.mechanical_hub.domain.entities.ServiceOrder;

import java.util.UUID;

public interface OrderStatusTransition {
    void execute(ServiceOrder order);
}
