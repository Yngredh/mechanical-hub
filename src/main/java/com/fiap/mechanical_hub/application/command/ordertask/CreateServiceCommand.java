package com.fiap.mechanical_hub.application.command.ordertask;

import com.fiap.mechanical_hub.application.dto.servicematerials.ServiceMaterialRequest;
import java.math.BigDecimal;
import java.util.List;

public record CreateServiceCommand(
    String name,
    String description,
    BigDecimal laborCost,
    BigDecimal basePrice,
    List<ServiceMaterialRequest> materials
) { }

