package com.fiap.mechanical_hub.application.dto.serviceorder;

import com.fiap.mechanical_hub.application.dto.service.ServiceResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class OrderTaskResponse {

    private UUID id;
    private UUID serviceOrderId;
    private ServiceResponse service;
    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
}

