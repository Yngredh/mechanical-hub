package com.fiap.mechanical_hub.application.usecases;

import com.fiap.mechanical_hub.application.dto.material.MaterialResponse;
import com.fiap.mechanical_hub.application.dto.material.UpsertMaterialRequest;
import com.fiap.mechanical_hub.application.mappers.MaterialMapper;
import com.fiap.mechanical_hub.domain.entities.Material;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.infrastructure.database.repositories.adapter.MaterialRepositoryAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class MaterialUseCase {

    public static final String MATERIAL_NAO_ENCONTRADO_PARA_O_ID = "Material não encontrado para o id: ";
    private final MaterialRepositoryAdapter materialRepository;
    private final MaterialMapper materialMapper;
    @Lazy
    private final StockUseCase stockUseCase;

    @Transactional
    public MaterialResponse create(UpsertMaterialRequest createRequest) {
        log.info("Creating new material with name: {}", createRequest.name());
        Material material = Material.create(
                createRequest.name(),
                createRequest.description(),
                createRequest.unitPrice(),
                createRequest.minStockQuantity()
        );

        Material savedMaterial = materialRepository.save(material);
        stockUseCase.setStockForNewMaterial(savedMaterial.getId());

        log.info("Material created with id: {}", savedMaterial.getId());
        return materialMapper.toResponse(savedMaterial);
    }

    public Material findById(UUID id) {
        return materialRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(MATERIAL_NAO_ENCONTRADO_PARA_O_ID + id));
    }
    
    @Transactional(readOnly = true)
    public MaterialResponse findMaterialById(UUID id) {
        log.info("Finding material with id: {}", id);
        Material material = findById(id);
        log.info("Material found: {}", material.getName());
        return materialMapper.toResponse(material);
    }

    @Transactional(readOnly = true)
    public List<MaterialResponse> findAll() {
        log.info("Finding all materials");
        return materialRepository.findAll().stream()
                .map(materialMapper::toResponse)
                .toList();
    }

    public MaterialResponse update(UUID id, UpsertMaterialRequest updateRequest) {
        log.info("Updating material with id: {}", id);
        Material existingMaterial = materialRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(MATERIAL_NAO_ENCONTRADO_PARA_O_ID + id));

        existingMaterial.update(
                updateRequest.name(),
                updateRequest.description(),
                updateRequest.unitPrice(),
                updateRequest.minStockQuantity());

        Material savedMaterial = materialRepository.save(existingMaterial);

        log.info("Material updated with id: {}", savedMaterial.getId());
        return materialMapper.toResponse(savedMaterial);
    }

    public void delete(UUID id) {
        log.info("Deleting material with id: {}", id);
        if (materialRepository.findById(id).isEmpty()) {
            throw new NotFoundException(MATERIAL_NAO_ENCONTRADO_PARA_O_ID + id);
        }

        materialRepository.deleteById(id);
        log.info("Material with id {} deleted successfully", id);
    }
}
