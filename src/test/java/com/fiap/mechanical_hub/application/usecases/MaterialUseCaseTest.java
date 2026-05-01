package com.fiap.mechanical_hub.application.usecases;

import com.fiap.mechanical_hub.application.dto.material.MaterialResponse;
import com.fiap.mechanical_hub.application.dto.material.UpsertMaterialRequest;
import com.fiap.mechanical_hub.application.mappers.MaterialMapper;
import com.fiap.mechanical_hub.domain.entities.Material;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.infrastructure.database.repositories.adapter.MaterialRepositoryAdapter;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do MaterialUseCase")
class MaterialUseCaseTest {

    @Mock
    private MaterialRepositoryAdapter materialRepository;

    @Mock
    private MaterialMapper materialMapper;

    @Mock
    private StockUseCase stockUseCase;

    @InjectMocks
    private MaterialUseCase materialUseCase;

    private UUID materialId;
    private Material material;
    private MaterialResponse materialResponse;
    private UpsertMaterialRequest upsertRequest;

    @BeforeEach
    void setUp() {
        materialId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        material = new Material(
                materialId,
                "Óleo de Motor",
                "Óleo sintético para motores",
                BigDecimal.valueOf(50.00),
                10,
                now,
                now
        );

        materialResponse = new MaterialResponse(
                materialId,
                "Óleo de Motor",
                "Óleo sintético para motores",
                BigDecimal.valueOf(50.00),
                10,
                now,
                now
        );

        upsertRequest = new UpsertMaterialRequest(
                "Óleo de Motor",
                "Óleo sintético para motores",
                BigDecimal.valueOf(50.00),
                10
        );
    }

    @Test
    @DisplayName("Deve criar material com sucesso")
    void shouldCreateMaterialSuccessfully() {
        Material createdMaterial = Material.create(
                "Óleo de Motor",
                "Óleo sintético para motores",
                BigDecimal.valueOf(50.00),
                10
        );
        when(materialRepository.save(any(Material.class))).thenReturn(createdMaterial);
        when(materialMapper.toResponse(createdMaterial)).thenReturn(materialResponse);
        doNothing().when(stockUseCase).setStockForNewMaterial(any(UUID.class));

        MaterialResponse result = materialUseCase.create(upsertRequest);

        assertNotNull(result);
        assertEquals("Óleo de Motor", result.name());
        verify(materialRepository).save(any(Material.class));
        verify(stockUseCase).setStockForNewMaterial(any(UUID.class));
        verify(materialMapper).toResponse(createdMaterial);
    }

    @Test
    @DisplayName("Deve encontrar material por ID com sucesso")
    void shouldFindMaterialByIdSuccessfully() {
        when(materialRepository.findById(materialId)).thenReturn(Optional.of(material));
        when(materialMapper.toResponse(material)).thenReturn(materialResponse);

        MaterialResponse result = materialUseCase.findMaterialById(materialId);

        assertNotNull(result);
        assertEquals(materialId, result.id());
        verify(materialRepository).findById(materialId);
        verify(materialMapper).toResponse(material);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando material não encontrado por ID")
    void shouldThrowNotFoundWhenMaterialNotFoundById() {
        when(materialRepository.findById(materialId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> materialUseCase.findMaterialById(materialId));
        verify(materialRepository).findById(materialId);
        verify(materialMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("Deve listar todos os materiais")
    void shouldFindAllMaterials() {
        List<Material> materials = List.of(material);
        when(materialRepository.findAll()).thenReturn(materials);
        when(materialMapper.toResponse(material)).thenReturn(materialResponse);

        List<MaterialResponse> result = materialUseCase.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(materialId, result.get(0).id());
        verify(materialRepository).findAll();
        verify(materialMapper).toResponse(material);
    }

    @Test
    @DisplayName("Deve atualizar material com sucesso")
    void shouldUpdateMaterialSuccessfully() {
        UpsertMaterialRequest updateRequest = new UpsertMaterialRequest(
                "Óleo de Motor Premium",
                "Óleo sintético premium para motores",
                BigDecimal.valueOf(70.00),
                15
        );

        Material updatedMaterial = new Material(
                materialId,
                "Óleo de Motor Premium",
                "Óleo sintético premium para motores",
                BigDecimal.valueOf(70.00),
                15,
                material.getCreatedAt(),
                LocalDateTime.now()
        );

        when(materialRepository.findById(materialId)).thenReturn(Optional.of(material));
        when(materialRepository.save(any(Material.class))).thenReturn(updatedMaterial);
        when(materialMapper.toResponse(updatedMaterial)).thenReturn(materialResponse);

        MaterialResponse result = materialUseCase.update(materialId, updateRequest);

        assertNotNull(result);
        verify(materialRepository).findById(materialId);
        verify(materialRepository).save(any(Material.class));
        verify(materialMapper).toResponse(updatedMaterial);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao atualizar material não encontrado")
    void shouldThrowNotFoundWhenUpdatingNonExistentMaterial() {
        UpsertMaterialRequest updateRequest = new UpsertMaterialRequest(
                "Óleo de Motor Premium",
                "Óleo sintético premium para motores",
                BigDecimal.valueOf(70.00),
                15
        );

        when(materialRepository.findById(materialId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> materialUseCase.update(materialId, updateRequest));
        verify(materialRepository).findById(materialId);
        verify(materialRepository, never()).save(any());
        verify(materialMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("Deve deletar material com sucesso")
    void shouldDeleteMaterialSuccessfully() {
        when(materialRepository.findById(materialId)).thenReturn(Optional.of(material));
        doNothing().when(materialRepository).deleteById(materialId);

        assertDoesNotThrow(() -> materialUseCase.delete(materialId));

        verify(materialRepository).findById(materialId);
        verify(materialRepository).deleteById(materialId);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao deletar material não encontrado")
    void shouldThrowNotFoundWhenDeletingNonExistentMaterial() {

        when(materialRepository.findById(materialId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> materialUseCase.delete(materialId));
        verify(materialRepository).findById(materialId);
        verify(materialRepository, never()).deleteById(any());
    }
}
