package com.fiap.mechanical_hub.domain.entities;

import com.fiap.mechanical_hub.domain.enums.OrderStatus;
import com.fiap.mechanical_hub.domain.exceptions.BusinessRuleException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ServiceOrder {

    private UUID id;
    private UUID vehicleId;
    private UUID customerId;
    private OrderStatus orderStatus;
    private UUID createdByUserId;
    private UUID responsibleUserId;
    private String orderNumber;
    private String requestDescription;
    private BigDecimal budget;
    private boolean hasStockPending;
    private LocalDateTime estimatedCompletionAt;
    private LocalDateTime openedAt;
    private LocalDateTime completedAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ServiceOrder create(UUID vehicleId, UUID customerId, String orderNumber, String requestDescription) {
        if (requestDescription == null || requestDescription.isBlank()) {
            throw new BusinessRuleException("A descrição da solicitação é obrigatória");
        }
        if (requestDescription.length() > 255) {
            throw new BusinessRuleException("A descrição da solicitação não pode ultrapassar 255 caracteres");
        }

        ServiceOrder order = new ServiceOrder();
        order.id = UUID.randomUUID();
        order.vehicleId = vehicleId;
        order.customerId = customerId;
        order.orderStatus = OrderStatus.RECEBIDA;
        order.createdByUserId = null;
        order.responsibleUserId = null;
        order.orderNumber = orderNumber;
        order.requestDescription = requestDescription;
        order.budget = null;
        order.hasStockPending = false;
        order.estimatedCompletionAt = null;
        order.openedAt = null;
        order.completedAt = null;
        order.deliveredAt = null;
        order.createdAt = LocalDateTime.now();
        order.updatedAt = LocalDateTime.now();

        return order;
    }
}
