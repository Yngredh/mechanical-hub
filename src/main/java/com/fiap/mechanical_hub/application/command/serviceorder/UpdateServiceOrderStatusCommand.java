package com.fiap.mechanical_hub.application.command.serviceorder;

import com.fiap.mechanical_hub.domain.enums.OrderStatusEnum;
import java.util.UUID;

public record UpdateServiceOrderStatusCommand(
    UUID orderId,
    OrderStatusEnum targetStatus,
    UUID userId
) { }

