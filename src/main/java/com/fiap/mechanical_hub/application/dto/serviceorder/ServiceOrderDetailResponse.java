package com.fiap.mechanical_hub.application.dto.serviceorder;

import com.fiap.mechanical_hub.application.dto.customer.CustomerResponse;
import com.fiap.mechanical_hub.application.dto.vehicle.VehicleResponse;
import com.fiap.mechanical_hub.domain.entities.OrderTask;
import com.fiap.mechanical_hub.domain.entities.ServiceData;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ServiceOrderDetailResponse(
        UUID id,
        String orderNumber,
        CustomerResponse customer,
        VehicleResponse vehiclePlate,
        String status,
        String requestDescription,
        BigDecimal budget,
        Boolean hasStockPending,
        List<ServiceData> serviceData,
        List<OrderTask> orderTasks,
        LocalDateTime createdAt
) {}