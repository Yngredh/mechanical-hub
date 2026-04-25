package com.fiap.mechanical_hub.application.dto.serviceorder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderTaskResponse {

    private UUID id;
    private UUID serviceOrderId;
    private UUID serviceId;
    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
}

