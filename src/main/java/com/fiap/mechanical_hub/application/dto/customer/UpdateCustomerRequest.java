package com.fiap.mechanical_hub.application.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCustomerRequest {
    private String name;
    private String telephone;
    private String email;
    private String address;
}
