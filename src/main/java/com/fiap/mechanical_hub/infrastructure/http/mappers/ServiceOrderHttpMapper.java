package com.fiap.mechanical_hub.infrastructure.http.mappers;

import com.fiap.mechanical_hub.application.command.serviceorder.CreateServiceOrderCommand;
import com.fiap.mechanical_hub.application.dto.customer.CustomerResponse;
import com.fiap.mechanical_hub.application.dto.serviceorder.CreateServiceOrderRequest;
import com.fiap.mechanical_hub.application.dto.serviceorder.ServiceOrderResponse;
import com.fiap.mechanical_hub.application.dto.serviceorder.request.ServiceOrderCustomerView;
import com.fiap.mechanical_hub.application.dto.vehicle.VehicleResponse;
import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ServiceOrderHttpMapper {

    public CreateServiceOrderCommand toCreateServiceOrderCommand(CreateServiceOrderRequest request, UUID createdByUserId) {
        return new CreateServiceOrderCommand(
                request.getCustomer().getName(),
                request.getCustomer().getDocumentType(),
                request.getCustomer().getDocumentNumber(),
                request.getCustomer().getTelephone(),
                request.getCustomer().getEmail(),
                request.getCustomer().getAddress(),
                request.getVehicle().getLicensePlate(),
                request.getVehicle().getBrand(),
                request.getVehicle().getModel(),
                request.getVehicle().getYear(),
                request.getVehicle().getColor(),
                request.getRequestDescription(),
                createdByUserId
        );
    }

    public ServiceOrderResponse toResponse(ServiceOrder order) {

        return new ServiceOrderResponse(
                order.getId(),
                order.getVehicleId(),
                order.getCustomerId(),
                order.getStatus().getDisplayName(),
                order.getCreatedByUserId(),
                order.getResponsibleUserId(),
                order.getOrderNumber(),
                order.getRequestDescription(),
                order.getBudget(),
                order.isHasStockPending(),
                order.getEstimatedCompletionAt(),
                order.getOpenedAt(),
                order.getCompletedAt(),
                order.getDeliveredAt(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                order.getOrderTasks().stream().map(OrderTaskHttpMapper::toTaskResponse).toList()
        );
    }

    public static ServiceOrderCustomerView toCustomerView(
            ServiceOrder order, VehicleResponse vehicle, CustomerResponse customer, List<String> services) {
        return new ServiceOrderCustomerView(
                order.getOrderNumber(),
                customer.getName(),
                vehicle.getLicensePlate(),
                vehicle.getModel(),
                vehicle.getBrand(),
                order.getStatus(),
                order.getBudget(),
                services,
                order.getOpenedAt(),
                order.getCompletedAt(),
                order.getDeliveredAt()
        );
    }
}
