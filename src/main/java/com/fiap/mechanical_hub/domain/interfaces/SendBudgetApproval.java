package com.fiap.mechanical_hub.domain.interfaces;

import com.fiap.mechanical_hub.domain.entities.ServiceOrder;

public interface SendBudgetApproval {
    void sendBudgetApprovalRequest(ServiceOrder os);
}
