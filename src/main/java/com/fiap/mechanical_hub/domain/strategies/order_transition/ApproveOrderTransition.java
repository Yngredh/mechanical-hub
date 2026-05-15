package com.fiap.mechanical_hub.domain.strategies.order_transition;

import com.fiap.mechanical_hub.domain.entities.ServiceOrder;

import java.util.UUID;

public class ApproveOrderTransition implements OrderStatusTransition {

    @Override
    public void execute(ServiceOrder order) {
        order.approve();
    }

}
