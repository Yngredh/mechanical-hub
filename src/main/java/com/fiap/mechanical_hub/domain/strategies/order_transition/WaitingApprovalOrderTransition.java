package com.fiap.mechanical_hub.domain.strategies.order_transition;

import com.fiap.mechanical_hub.domain.interfaces.SendBudgetApproval;
import com.fiap.mechanical_hub.domain.entities.ServiceOrder;

public class WaitingApprovalOrderTransition implements OrderStatusTransition {

    private final SendBudgetApproval sendBudgetApprovalApprovalRequest;

    public WaitingApprovalOrderTransition(SendBudgetApproval sendBudgetApprovalApprovalRequest) {
        this.sendBudgetApprovalApprovalRequest = sendBudgetApprovalApprovalRequest;
    }

    @Override
    public void execute(ServiceOrder order) {
        order.submitForApproval();
        sendBudgetApprovalApprovalRequest.sendBudgetApprovalRequest(order);
    }
}
