package com.fiap.mechanical_hub.domain.service;

import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.domain.exceptions.BusinessRuleException;

import java.util.List;

public class ServiceOrderDomainService {

    public void hasAnyOpenServiceOrder(List<ServiceOrder> orders) {
        orders.forEach(order -> {
            if (order.isOrderOpen()) {
                throw new BusinessRuleException("Essa ação não pode ser executada pois há ordens abertas");
            }
        });
    }

}
