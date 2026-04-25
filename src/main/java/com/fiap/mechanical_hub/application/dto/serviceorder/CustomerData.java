package com.fiap.mechanical_hub.application.dto.serviceorder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerData {
    private String name;
    private String documentType;
    private String documentNumber;
    private String telephone;
    private String email;
    private String address;
}
