package com.fiap.mechanical_hub.application.dto.serviceorder;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpenServiceOrderRequest {

    @NotNull(message = "Customer ID is required")
    private UUID customerId;

    @NotNull(message = "Vehicle ID is required")
    private UUID vehicleId;

    @NotEmpty(message = "At least one service ID is required")
    private List<UUID> serviceIds;

    @NotNull(message = "Request description is required")
    private String requestDescription;
}

