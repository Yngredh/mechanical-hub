package com.fiap.mechanical_hub.application.dto.servicematerials;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class ServiceMaterialResponse {

    private UUID materialId;
    private String materialName;
    private String materialDescription;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal totalCost;
}

