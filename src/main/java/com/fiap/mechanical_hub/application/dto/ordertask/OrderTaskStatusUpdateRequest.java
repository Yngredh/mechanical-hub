package com.fiap.mechanical_hub.application.dto.ordertask;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderTaskStatusUpdateRequest {
    private String status;
}
