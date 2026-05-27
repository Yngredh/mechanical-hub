package com.fiap.mechanical_hub.application.dto.serviceorder;

import com.fiap.mechanical_hub.application.dto.customer.InsertCustomerRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateServiceOrderRequest {
    private InsertCustomerRequest customer;
    private InsertVehicleRequest vehicle;
    private String requestDescription;
}
