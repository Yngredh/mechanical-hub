package com.fiap.mechanical_hub.application.interfaces;

public interface BudgetApprovalTrigger {
    void sendBudgetApprovalRequest(String customerId, String orderNumber);
    void budgetApprovalReceived(String orderNumber);
}
