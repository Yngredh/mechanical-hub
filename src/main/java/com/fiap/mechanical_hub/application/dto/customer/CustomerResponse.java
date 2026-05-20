package com.fiap.mechanical_hub.application.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {
    private UUID id;
    private String name;
    private String documentNumber;
    private String email;
    private String telephone;
}

