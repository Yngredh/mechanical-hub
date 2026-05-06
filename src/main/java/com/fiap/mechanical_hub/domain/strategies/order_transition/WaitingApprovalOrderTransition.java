package com.fiap.mechanical_hub.domain.strategies.order_transition;

import com.fiap.mechanical_hub.application.interfaces.SendBudgetApproval;
import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import lombok.AllArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
public class WaitingApprovalOrderTransition implements OrderStatusTransition {

    private final SendBudgetApproval sendBudgetApprovalApprovalRequest;

    @Override
    public void execute(ServiceOrder order, UUID userId) {
        order.submitForApproval();
        sendBudgetApprovalApprovalRequest.sendBudgetApprovalRequest(order);
    }
}
