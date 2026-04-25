package com.fiap.mechanical_hub.application.usecases;

import com.fiap.mechanical_hub.application.dto.serviceorder.CreateServiceOrderRequest;
import com.fiap.mechanical_hub.application.dto.serviceorder.ServiceOrderResponse;
import com.fiap.mechanical_hub.application.mappers.ServiceOrderMapper;
import com.fiap.mechanical_hub.domain.entities.Customer;
import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.domain.entities.Vehicle;
import com.fiap.mechanical_hub.domain.repositories.ServiceOrderRepository;
import com.fiap.mechanical_hub.shared.utils.OrderNumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ServiceOrderService {

    private final ServiceOrderRepository serviceOrderRepository;
    private final CustomerService customerService;
    private final VehicleService vehicleService;
    private final OrderNumberGenerator orderNumberGenerator;
    private final ServiceOrderMapper serviceOrderMapper;

    public ServiceOrderResponse create(CreateServiceOrderRequest request) {
        var customerData = request.getCustomer();
        Customer customer = customerService.findByDocumentOrCreate(
                customerData.getName(),
                customerData.getDocumentType(),
                customerData.getDocumentNumber(),
                customerData.getTelephone(),
                customerData.getEmail(),
                customerData.getAddress()
        );

        var vehicleData = request.getVehicle();
        Vehicle vehicle = vehicleService.findByLicensePlateOrCreate(
                customer.getId(),
                vehicleData.getLicensePlate(),
                vehicleData.getBrand(),
                vehicleData.getModel(),
                vehicleData.getYear(),
                vehicleData.getColor()
        );

        String orderNumber = orderNumberGenerator.generate();
        ServiceOrder serviceOrder = ServiceOrder.create(vehicle.getId(), customer.getId(), orderNumber, request.getRequestDescription());
        ServiceOrder saved = serviceOrderRepository.save(serviceOrder);

        return serviceOrderMapper.toResponse(saved, customer, vehicle);
    }
}
