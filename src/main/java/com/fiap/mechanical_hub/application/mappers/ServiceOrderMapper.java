package com.fiap.mechanical_hub.application.mappers;

import com.fiap.mechanical_hub.application.dto.serviceorder.ServiceOrderResponse;
import com.fiap.mechanical_hub.domain.entities.Customer;
import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.domain.entities.Vehicle;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ServiceOrderMapper {

    private final CustomerMapper customerMapper;
    private final VehicleMapper vehicleMapper;

    public ServiceOrderResponse toResponse(ServiceOrder serviceOrder, Customer customer, Vehicle vehicle) {
        return new ServiceOrderResponse(
                serviceOrder.getId(),
                serviceOrder.getOrderNumber(),
                serviceOrder.getOrderStatus().getValue(),
                serviceOrder.getRequestDescription(),
                serviceOrder.getBudget(),
                serviceOrder.isHasStockPending(),
                serviceOrder.getResponsibleUserId(),
                customerMapper.toResponse(customer),
                vehicleMapper.toResponse(vehicle),
                serviceOrder.getEstimatedCompletionAt(),
                serviceOrder.getOpenedAt(),
                serviceOrder.getCompletedAt(),
                serviceOrder.getDeliveredAt(),
                serviceOrder.getCreatedAt(),
                serviceOrder.getUpdatedAt()
        );
    }
}
