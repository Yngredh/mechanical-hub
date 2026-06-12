package com.fiap.mechanical_hub.application.mappers;

import com.fiap.mechanical_hub.application.command.serviceorder.CreateServiceOrderCommand;
import com.fiap.mechanical_hub.application.command.serviceorder.OpenServiceOrderCommand;
import com.fiap.mechanical_hub.application.dto.customer.CustomerResponse;
import com.fiap.mechanical_hub.application.dto.serviceorder.CreateServiceOrderRequest;
import com.fiap.mechanical_hub.application.dto.serviceorder.InsertVehicleRequest;
import com.fiap.mechanical_hub.application.dto.serviceorder.OpenServiceOrderRequest;
import com.fiap.mechanical_hub.application.dto.serviceorder.ServiceOrderResponse;
import com.fiap.mechanical_hub.application.dto.serviceorder.request.ServiceOrderCustomerView;
import com.fiap.mechanical_hub.application.dto.vehicle.VehicleResponse;
import com.fiap.mechanical_hub.application.dto.customer.InsertCustomerRequest;
import com.fiap.mechanical_hub.domain.entities.ServiceOrder;
import com.fiap.mechanical_hub.mocks.domain.entities.ServiceOrderMock;
import com.fiap.mechanical_hub.mocks.domain.entities.VehicleMock;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ServiceOrderMapperTest {

    private static final UUID USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000004");
    private static final UUID CUSTOMER_ID = UUID.fromString("00000000-0000-0000-0000-000000000003");
    private static final UUID VEHICLE_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final UUID SERVICE_ID_1 = UUID.fromString("00000000-0000-0000-0000-000000000040");

    private final ServiceOrderMapper mapper = new ServiceOrderMapper();

    private CreateServiceOrderRequest buildCreateRequest() {
        InsertCustomerRequest customer = new InsertCustomerRequest(
                "João Silva", "CPF", "52998224725", "5511987654321", "joao@email.com", "Rua A, 123"
        );
        InsertVehicleRequest vehicle = new InsertVehicleRequest("ABC1234", "Toyota", "Corolla", 2022, "Prata");
        return new CreateServiceOrderRequest(customer, vehicle, "Diagnóstico e reparo");
    }

    private VehicleResponse buildVehicleResponse() {
        return new VehicleResponse(
                VEHICLE_ID, CUSTOMER_ID, "ABC1234", "Toyota", "Corolla", 2022, "Prata",
                LocalDateTime.now(), LocalDateTime.now()
        );
    }

    private CustomerResponse buildCustomerResponse() {
        return new CustomerResponse(CUSTOMER_ID, "João Silva", "529.982.247-25", "joao@email.com", "+55 (11) 9 8765-4321");
    }

    @Test
    void shouldMapAllFields_whenConvertingToCreateServiceOrderCommand() {
        CreateServiceOrderRequest request = buildCreateRequest();

        CreateServiceOrderCommand command = mapper.toCreateServiceOrderCommand(request, USER_ID);

        assertThat(command.createdByUserId()).isEqualTo(USER_ID);
        assertThat(command.customerName()).isEqualTo(request.getCustomer().getName());
        assertThat(command.documentType()).isEqualTo(request.getCustomer().getDocumentType());
        assertThat(command.documentNumber()).isEqualTo(request.getCustomer().getDocumentNumber());
        assertThat(command.licensePlate()).isEqualTo(request.getVehicle().getLicensePlate());
        assertThat(command.requestDescription()).isEqualTo(request.getRequestDescription());
    }

    @Test
    void shouldMapAllFields_whenConvertingToOpenServiceOrderCommand() {
        OpenServiceOrderRequest request = new OpenServiceOrderRequest(
                CUSTOMER_ID, VEHICLE_ID, List.of(SERVICE_ID_1), "Revisão completa"
        );

        OpenServiceOrderCommand command = mapper.toOpenServiceOrderCommand(request, USER_ID);

        assertThat(command.customerId()).isEqualTo(request.getCustomerId());
        assertThat(command.vehicleId()).isEqualTo(request.getVehicleId());
        assertThat(command.serviceIds()).isEqualTo(request.getServiceIds());
        assertThat(command.requestDescription()).isEqualTo(request.getRequestDescription());
        assertThat(command.createdByUserId()).isEqualTo(USER_ID);
    }

    @Test
    void shouldMapAllFields_whenConvertingServiceOrderToResponse() {
        ServiceOrder order = ServiceOrderMock.received();

        ServiceOrderResponse response = mapper.toResponse(order);

        assertThat(response.getId()).isEqualTo(order.getId());
        assertThat(response.getVehicleId()).isEqualTo(order.getVehicleId());
        assertThat(response.getCustomerId()).isEqualTo(order.getCustomerId());
        assertThat(response.getOrderNumber()).isEqualTo(order.getOrderNumber());
        assertThat(response.getRequestDescription()).isEqualTo(order.getRequestDescription());
        assertThat(response.isHasStockPending()).isEqualTo(order.isHasStockPending());
    }

    @Test
    void shouldMapStatusDisplayName_whenConvertingServiceOrderToResponse() {
        ServiceOrder order = ServiceOrderMock.received();

        ServiceOrderResponse response = mapper.toResponse(order);

        assertThat(response.getStatus()).isEqualTo(order.getStatus().getDisplayName());
    }

    @Test
    void shouldMapOrderTasks_whenServiceOrderHasTasks() {
        ServiceOrder order = ServiceOrderMock.withOneUnfinishedTask();

        ServiceOrderResponse response = mapper.toResponse(order);

        assertThat(response.getOrderTasks()).hasSize(1);
    }

    @Test
    void shouldMapAllFields_whenConvertingToCustomerView() {
        ServiceOrder order = ServiceOrderMock.waitingApproval();
        VehicleResponse vehicle = buildVehicleResponse();
        CustomerResponse customer = buildCustomerResponse();
        List<String> services = List.of("Troca de óleo");

        ServiceOrderCustomerView view = ServiceOrderMapper.toCustomerView(order, vehicle, customer, services);

        assertThat(view.orderNumber()).isEqualTo(order.getOrderNumber());
        assertThat(view.customerName()).isEqualTo(customer.getName());
        assertThat(view.vehicleLicensePlate()).isEqualTo(vehicle.getLicensePlate());
        assertThat(view.vehicleModel()).isEqualTo(vehicle.getModel());
        assertThat(view.vehicleBrand()).isEqualTo(vehicle.getBrand());
        assertThat(view.status()).isEqualTo(order.getStatus());
        assertThat(view.budget()).isEqualTo(order.getBudget());
        assertThat(view.services()).isEqualTo(services);
    }
}
