package com.fiap.mechanical_hub.application.usecases;

import com.fiap.mechanical_hub.application.dto.service.UpsertServiceRequest;
import com.fiap.mechanical_hub.application.dto.servicematerials.ServiceMaterialRequest;
import com.fiap.mechanical_hub.application.repositories.ServiceRepository;
import com.fiap.mechanical_hub.domain.entities.Material;
import com.fiap.mechanical_hub.domain.entities.ServiceData;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceUseCaseTest {

    @Mock
    private MaterialUseCase materialUseCase;

    @Mock
    private ServiceRepository serviceRepository;

    @InjectMocks
    private ServiceUseCase serviceUseCase;

    private Material material;
    private UUID materialId;

    @BeforeEach
    void setUp() {
        materialId = UUID.randomUUID();
        material = new Material(
                materialId, "Óleo", "Lubrificante",
                new BigDecimal("50.00"), 10,
                LocalDateTime.now(), LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Deve criar um serviço calculando o preço total corretamente")
    void create_ShouldReturnServiceResponse_WithCalculatedPrice() {
        var materialReq = new ServiceMaterialRequest(materialId, 2); // Custo: 100.00
        var request = new UpsertServiceRequest(
                "Troca de Óleo", "Serviço padrão",
                new BigDecimal("80.00"), // Mão de obra
                new BigDecimal("180.00"),
                List.of(materialReq)
        );

        when(materialUseCase.findById(materialId)).thenReturn(material);
        when(serviceRepository.save(any(ServiceData.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = serviceUseCase.create(request);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Troca de Óleo");
        assertThat(response.getTotalPrice()).isEqualByComparingTo(new BigDecimal("180.00"));

        verify(materialUseCase, times(1)).findById(materialId);
        verify(serviceRepository, times(1)).save(any(ServiceData.class));
    }

    @Test
    @DisplayName("Deve atualizar um serviço existente com sucesso")
    void update_ShouldModifyExistingService() {
        UUID serviceId = UUID.randomUUID();
        ServiceData existingService = ServiceData.create(
                "Nome Antigo", "Desc", BigDecimal.TEN, BigDecimal.TEN, List.of()
        );

        var materialReq = new ServiceMaterialRequest(materialId, 1);
        var request = new UpsertServiceRequest(
                "Nome Novo", "Nova Desc", BigDecimal.ZERO, BigDecimal.ZERO, List.of(materialReq)
        );

        when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(existingService));
        when(materialUseCase.findById(materialId)).thenReturn(material);

        var response = serviceUseCase.update(serviceId, request);

        assertThat(response.getName()).isEqualTo("Nome Novo");
        verify(serviceRepository).save(existingService);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao tentar atualizar serviço inexistente")
    void update_ShouldThrowNotFound_WhenIdDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(serviceRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> serviceUseCase.update(id, mock(UpsertServiceRequest.class)))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Service not found");
    }

    @Test
    @DisplayName("Deve buscar serviço por ID com sucesso")
    void findById_ShouldReturnResponse() {
        UUID id = UUID.randomUUID();
        ServiceData serviceData = ServiceData.create("Teste", "Desc", BigDecimal.TEN, BigDecimal.TEN, List.of());
        when(serviceRepository.findById(id)).thenReturn(Optional.of(serviceData));

        var response = serviceUseCase.findById(id);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Teste");
    }

    @Test
    @DisplayName("Deve retornar lista de todos os serviços")
    void findAll_ShouldReturnList() {
        ServiceData s1 = ServiceData.create("S1", "D1", BigDecimal.ONE, BigDecimal.ONE, List.of());
        when(serviceRepository.findAll()).thenReturn(List.of(s1));

        var result = serviceUseCase.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("S1");
    }

    @Test
    @DisplayName("Deve chamar o repositório para deletar serviço")
    void delete_ShouldInvokeRepository() {
        UUID id = UUID.randomUUID();

        serviceUseCase.delete(id);

        verify(serviceRepository, times(1)).deleteById(id);
    }
}
