package com.fiap.mechanical_hub.application.usecases;

import com.fiap.mechanical_hub.application.dto.serviceorder.AddServicesToOrderRequest;
import com.fiap.mechanical_hub.application.dto.serviceorder.CreateServiceOrderRequest;
import com.fiap.mechanical_hub.application.dto.serviceorder.ServiceOrderResponse;
import com.fiap.mechanical_hub.application.mappers.ServiceOrderMapper;
import com.fiap.mechanical_hub.application.repositories.ServiceOrderRepository;
import com.fiap.mechanical_hub.domain.entities.*;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.shared.utils.OrderNumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@org.springframework.stereotype.Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ServiceOrderUseCase {

    private final ServiceOrderRepository serviceOrderRepository;
    private final ServiceMaterialUseCase serviceMaterialUseCase;
    private final StockUseCase stockUseCase;
    private final CustomerUseCase customerUseCase;
    private final VehicleUseCase vehicleUseCase;
    private final ServiceUseCase serviceUseCase;
    private final OrderNumberGenerator orderNumberGenerator;
    private final ServiceOrderMapper serviceOrderMapper;

    public ServiceOrderResponse create(CreateServiceOrderRequest request, UUID createdByUserId) {
        var customerData = request.getCustomer();
        Customer customer = customerUseCase.findByDocumentOrCreate(
                customerData.getName(),
                customerData.getDocumentType(),
                customerData.getDocumentNumber(),
                customerData.getTelephone(),
                customerData.getEmail(),
                customerData.getAddress()
        );

        var vehicleData = request.getVehicle();
        Vehicle vehicle = vehicleUseCase.findByLicensePlateOrCreate(
                customer.getId(),
                vehicleData.getLicensePlate(),
                vehicleData.getBrand(),
                vehicleData.getModel(),
                vehicleData.getYear(),
                vehicleData.getColor()
        );

        String orderNumber = orderNumberGenerator.generate();
        ServiceOrder serviceOrder = ServiceOrder.create(vehicle.getId(), customer.getId(), orderNumber, request.getRequestDescription(), createdByUserId);
        ServiceOrder saved = serviceOrderRepository.save(serviceOrder);

        return serviceOrderMapper.toResponse(saved);
    }

    @Transactional
    public void addServicesToOrder(UUID serviceOrderId, AddServicesToOrderRequest request) {
        log.info("Adding {} services to service order {}", request.serviceIds().size(), serviceOrderId);

        ServiceOrder order = serviceOrderRepository.findById(serviceOrderId)
                .orElseThrow(() -> new NotFoundException("Ordem de serviço não encontrada com ID: " + serviceOrderId));

        order.isAddingServiceAvailable();

        boolean hasStockPending = false;

        BigDecimal totalBudget = order.getBudget() != null ? order.getBudget() : BigDecimal.ZERO;

        for (UUID serviceId : request.serviceIds()) {
            Service service = serviceUseCase.findServiceById(serviceId);

            log.info("Processing service {} for order {}", serviceId, serviceOrderId);

            List<ServiceMaterial> serviceMaterials = serviceMaterialUseCase.getServiceMaterials(serviceId);

            for (ServiceMaterial serviceMaterial : serviceMaterials) {
                UUID materialId = serviceMaterial.getMaterial().getId();
                int quantity = serviceMaterial.getQuantity();

                log.info("Reserving {} units of material {} for service {}", quantity, materialId, serviceId);

                hasStockPending = stockUseCase.reserveMaterials(order, serviceMaterial.getMaterial(), quantity);
            }

            totalBudget = totalBudget.add(service.getTotalPrice());
        }

        order.updateBudget(totalBudget);
        order.setHasStockPending(hasStockPending);

        serviceOrderRepository.save(order);

        log.info("Successfully added services to service order {}. Has pending items: {}", serviceOrderId, hasStockPending);
    }

}
