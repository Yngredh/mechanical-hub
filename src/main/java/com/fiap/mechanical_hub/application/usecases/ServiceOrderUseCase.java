package com.fiap.mechanical_hub.application.usecases;

import com.fiap.mechanical_hub.application.dto.customer.CustomerResponse;
import com.fiap.mechanical_hub.application.dto.serviceorder.*;
import com.fiap.mechanical_hub.application.dto.serviceorder.request.ServiceOrderCustomerView;
import com.fiap.mechanical_hub.application.dto.vehicle.VehicleResponse;
import com.fiap.mechanical_hub.application.interfaces.SendBudgetApproval;
import com.fiap.mechanical_hub.application.mappers.ServiceOrderMapper;
import com.fiap.mechanical_hub.application.repositories.ServiceOrderRepository;
import com.fiap.mechanical_hub.domain.entities.*;
import com.fiap.mechanical_hub.domain.enums.OrderStatusEnum;
import com.fiap.mechanical_hub.domain.enums.TaskStatusEnum;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.domain.strategies.order_transition.OrderStatusTransitionFactory;
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

    private final ServiceOrderRepository repository;

    private final ServiceMaterialUseCase serviceMaterialUseCase;
    private final StockUseCase stockUseCase;
    private final CustomerUseCase customerUseCase;
    private final VehicleUseCase vehicleUseCase;
    private final ServiceUseCase serviceUseCase;

    private final OrderNumberGenerator orderNumberGenerator;
    private final ServiceOrderMapper mapper;
    private final OrderStatusTransitionFactory factory;
    private final SendBudgetApproval sendBudgetApprovalApprovalRequest;

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
        ServiceOrder serviceOrder = ServiceOrder.create(
                vehicle.getId(), customer.getId(), orderNumber, request.getRequestDescription(), createdByUserId);
        ServiceOrder saved = repository.save(serviceOrder);

        return mapper.toResponse(saved);
    }

    @Transactional
    public void addServices(UUID serviceOrderId, AddServicesToOrderRequest request) {
        log.info("Adding {} services to service order {}", request.serviceIds().size(), serviceOrderId);

        ServiceOrder order = repository.findById(serviceOrderId)
                .orElseThrow(() -> new NotFoundException("Ordem de serviço não encontrada com ID: " + serviceOrderId));

        order.isAddingServiceAvailable();

        boolean hasStockPending = false;

        BigDecimal totalBudget = order.getBudget() != null ? order.getBudget() : BigDecimal.ZERO;

        for (UUID serviceId : request.serviceIds()) {
            if (order.validateTaskNotDuplicated(serviceId)) break;

            Service service = serviceUseCase.findServiceById(serviceId);

            log.info("Processing service {} for order {}", serviceId, serviceOrderId);

            List<ServiceMaterial> serviceMaterials = serviceMaterialUseCase.getServiceMaterials(serviceId);

            for (ServiceMaterial sm : serviceMaterials) {
                UUID materialId = sm.getMaterial().getId();
                int quantity = sm.getQuantity();

                log.info("Reserving {} units of material {} for service {}", quantity, materialId, serviceId);

                hasStockPending = hasStockPending || stockUseCase.reserveMaterial(order, sm.getMaterial(), sm.getQuantity());
            }

            totalBudget = totalBudget.add(service.getTotalPrice());
            order.addTask(OrderTask.create(order.getId(), service));
        }

        order.updateBudget(totalBudget);
        order.setHasStockPending(hasStockPending);
        repository.save(order);

        log.info("Successfully added services to service order {}. Has pending items: {}", serviceOrderId, hasStockPending);
    }

    public ServiceOrder updateOrderStatus(UUID orderId, OrderStatusEnum targetStatus) {
        ServiceOrder order = repository.findById(orderId).orElseThrow();
        factory.get(targetStatus).execute(order);
        return repository.save(order);
    }

    public void submitOrder(UUID orderId){
        ServiceOrder order = repository.findById(orderId).orElseThrow();
        order.submitForApproval();
        sendBudgetApprovalApprovalRequest.sendBudgetApprovalRequest(order);
        repository.save(order);
    }

    public List<ServiceOrderSummaryResponse> findAll() {
        return repository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(ServiceOrderMapper::toSummaryResponse).toList();
    }

     public ServiceOrderDetailResponse findById(UUID id) {
        ServiceOrder order = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ordem de serviço não encontrada com ID: " + id));

        VehicleResponse vehicle = vehicleUseCase.findById(order.getVehicleId());
        CustomerResponse customer = customerUseCase.findById(order.getCustomerId());
         List<Service> services = order.getOrderTasks()
                 .stream()
                 .map(OrderTask::getService)
                 .toList();

        return ServiceOrderMapper.toDetailResponse(order, vehicle, customer, services);
    }

    public void approve(UUID serviceOrderId) {
        ServiceOrder order = repository.findById(serviceOrderId)
                .orElseThrow(() -> new NotFoundException("Service order with id " + serviceOrderId + " not found"));

        factory.get(OrderStatusEnum.APROVADO).execute(order);
        repository.save(order);
    }

    public void reject(UUID serviceOrderId) {
        ServiceOrder order = repository.findById(serviceOrderId)
                .orElseThrow(() -> new NotFoundException("Service order with id " + serviceOrderId + " not found"));

        factory.get(OrderStatusEnum.RECUSADO).execute(order);
        repository.save(order);
    }

    public void updateTaskStatus(UUID id, UUID taskId, TaskStatusEnum status) {
        ServiceOrder order = repository.findById(id).orElseThrow();
        switch (status) {
            case TaskStatusEnum.INICIADO -> order.startTask(taskId);
            case TaskStatusEnum.FINALIZADO -> order.finishTask(taskId);
            default -> throw new IllegalArgumentException("Status não reconhecido para atualização: " + status);
        }
        repository.save(order);
    }

    public ServiceOrderCustomerView findByOrderNumber(String orderNumber) {
        ServiceOrder order = repository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new NotFoundException("Ordem de serviço não encontrada com número: " + orderNumber));

        VehicleResponse vehicle = vehicleUseCase.findById(order.getVehicleId());
        CustomerResponse customer = customerUseCase.findById(order.getCustomerId());
        List<String> services = order.getOrderTasks()
                .stream()
                .map(task -> task.getService().getName())
                .toList();

        return mapper.toCustomerView(order, vehicle, customer, services);
    }

}
