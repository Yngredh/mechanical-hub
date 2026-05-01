package com.fiap.mechanical_hub.application.usecases;

import com.fiap.mechanical_hub.application.dto.customer.CustomerResponse;
import com.fiap.mechanical_hub.application.dto.serviceorder.*;
import com.fiap.mechanical_hub.application.dto.vehicle.VehicleResponse;
import com.fiap.mechanical_hub.application.mappers.ServiceOrderMapper;
import com.fiap.mechanical_hub.application.repositories.ServiceOrderRepository;
import com.fiap.mechanical_hub.domain.entities.*;
import com.fiap.mechanical_hub.domain.enums.OrderStatusEnum;
import com.fiap.mechanical_hub.domain.strategies.order_transition.OrderStatusTransition;
import com.fiap.mechanical_hub.domain.strategies.order_transition.OrderStatusTransitionFactory;
import com.fiap.mechanical_hub.shared.utils.OrderNumberGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceOrderUseCaseTest {

    @Mock private ServiceOrderRepository repository;
    @Mock private ServiceMaterialUseCase serviceMaterialUseCase;
    @Mock private StockUseCase stockUseCase;
    @Mock private CustomerUseCase customerUseCase;
    @Mock private VehicleUseCase vehicleUseCase;
    @Mock private ServiceUseCase serviceUseCase;
    @Mock private OrderNumberGenerator orderNumberGenerator;
    @Mock private ServiceOrderMapper mapper;
    @Mock private OrderStatusTransitionFactory factory;

    @InjectMocks
    private ServiceOrderUseCase useCase;

    @Test
    @DisplayName("Deve criar uma Ordem de Serviço com sucesso")
    void create_ShouldReturnResponse_WhenRequestIsValid() {
        UUID userId = UUID.randomUUID();
        var request = createMockRequest(); // Método auxiliar para mockar o DTO complexo

        Customer customer = mock(Customer.class);
        when(customer.getId()).thenReturn(UUID.randomUUID());
        when(customerUseCase.findByDocumentOrCreate(any(), any(), any(), any(), any(), any())).thenReturn(customer);

        Vehicle vehicle = mock(Vehicle.class);
        when(vehicle.getId()).thenReturn(UUID.randomUUID());
        when(vehicleUseCase.findByLicensePlateOrCreate(any(), any(), any(), any(), any(), any())).thenReturn(vehicle);

        when(orderNumberGenerator.generate()).thenReturn("OS-202401-0001");
        when(repository.save(any(ServiceOrder.class))).thenAnswer(i -> i.getArgument(0));
        when(mapper.toResponse(any(ServiceOrder.class))).thenReturn(mock(ServiceOrderResponse.class));

        var response = useCase.create(request, userId);

        assertThat(response).isNotNull();
        verify(repository).save(any(ServiceOrder.class));
        verify(orderNumberGenerator).generate();
    }

    @Test
    @DisplayName("Deve adicionar serviços e reservar estoque corretamente")
    void addServices_ShouldProcessStockAndBudget() {
        UUID orderId = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();
        AddServicesToOrderRequest request = new AddServicesToOrderRequest(List.of(serviceId));

        ServiceOrder order = spy(ServiceOrder.create(UUID.randomUUID(), UUID.randomUUID(), "OS-1", "Desc", UUID.randomUUID()));
        ServiceData serviceData = mock(ServiceData.class);
        Material material = mock(Material.class);
        ServiceMaterial sm = new ServiceMaterial(UUID.randomUUID(), serviceId, material, 2);

        when(order.getStatus()).thenReturn(OrderStatusEnum.EM_DIAGNOSTICO);
        when(repository.findById(orderId)).thenReturn(Optional.of(order));
        when(serviceUseCase.findServiceById(serviceId)).thenReturn(serviceData);
        when(serviceMaterialUseCase.getServiceMaterials(serviceId)).thenReturn(List.of(sm));
        when(serviceData.getTotalPrice()).thenReturn(new BigDecimal("150.00"));
        when(stockUseCase.reserveForServiceOrder(any(), any(), anyInt())).thenReturn(true);

        useCase.addServices(orderId, request);

        verify(order).addTask(any());
        verify(order).updateBudget(new BigDecimal("150.00"));
        verify(order).setHasStockPending(true);
        verify(repository).save(order);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao tentar atualizar status de OS inexistente")
    void updateOrderStatus_ShouldThrowException_WhenOrderNotFound() {
        UUID orderId = UUID.randomUUID();
        when(repository.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.updateOrderStatus(orderId, OrderStatusEnum.APROVADO))
                .isInstanceOf(java.util.NoSuchElementException.class);
    }

    @Test
    @DisplayName("Deve executar a transição de status usando a Factory")
    void approve_ShouldUseFactoryAndSave() {
        UUID orderId = UUID.randomUUID();
        ServiceOrder order = mock(ServiceOrder.class);
        OrderStatusTransition transition = mock(OrderStatusTransition.class);

        when(repository.findById(orderId)).thenReturn(Optional.of(order));
        when(factory.get(OrderStatusEnum.APROVADO)).thenReturn(transition);

        useCase.approve(orderId);

        verify(transition).execute(order);
        verify(repository).save(order);
    }

    @Test
    @DisplayName("Deve buscar detalhes da OS integrando múltiplos UseCases")
    void findById_ShouldReturnDetailResponse() {
        UUID id = UUID.randomUUID();
        ServiceOrder order = mock(ServiceOrder.class);
        when(order.getVehicleId()).thenReturn(UUID.randomUUID());
        when(order.getCustomerId()).thenReturn(UUID.randomUUID());
        when(order.getStatus()).thenReturn(OrderStatusEnum.APROVADO);
        when(repository.findById(id)).thenReturn(Optional.of(order));
        when(vehicleUseCase.findById(any())).thenReturn(new VehicleResponse());
        when(customerUseCase.findById(any())).thenReturn(new CustomerResponse());

        var result = useCase.findById(id);

        assertThat(result).isNotNull();
        verify(repository).findById(id);
    }

    private CreateServiceOrderRequest createMockRequest() {
        var customerView = new CustomerData();
        customerView.setName("João");
        customerView.setDocumentNumber("123");

        var vehicleView = new VehicleData();
        vehicleView.setLicensePlate("ABC-1234");

        var request = new CreateServiceOrderRequest();
        request.setCustomer(customerView);
        request.setVehicle(vehicleView);
        request.setRequestDescription("Troca de óleo");
        return request;
    }
}