package com.fiap.mechanical_hub.application.dto.serviceorder.request;

import com.fiap.mechanical_hub.domain.enums.OrderStatusEnum;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ServiceOrderCustomerView(
        String orderNumber,
        String customerName,
        String vehicleLicensePlate,
        String vehicleModel,
        String vehicleBrand,
        OrderStatusEnum status,
        BigDecimal budget,
        List<String> services,
        LocalDateTime openedAt,
        LocalDateTime completedAt,
        LocalDateTime deliveredAt
) {}