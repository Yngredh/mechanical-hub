package com.fiap.mechanical_hub.application.services;

import com.fiap.mechanical_hub.application.dto.material.UpsertMaterialRequest;
import com.fiap.mechanical_hub.application.dto.material.MaterialResponse;
import com.fiap.mechanical_hub.application.mappers.MaterialMapper;
import com.fiap.mechanical_hub.domain.entities.Material;
import com.fiap.mechanical_hub.domain.repositories.MaterialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final MaterialMapper materialMapper;

    public MaterialResponse create(UpsertMaterialRequest createRequest) {
        log.info("Creating new material with name: {}", createRequest.name());
        Material material = Material.create(
                createRequest.name(),
                createRequest.description(),
                createRequest.unitPrice(),
                createRequest.minStockQuantity()
        );

        Material savedMaterial = materialRepository.save(material);

        log.info("Material created with id: {}", savedMaterial.getId());
        return materialMapper.toResponse(savedMaterial);
    }

    @Transactional(readOnly = true)
    public MaterialResponse findById(UUID id) {
        log.info("Finding material with id: {}", id);
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Material não encontrado para o id: " + id));
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
                .orElseThrow(() -> new NoSuchElementException("Material não encontrado para o id: " + id));

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
            throw new NoSuchElementException("Material não encontrado para o id: " + id);
        }
        log.info("Material with id {} deleted successfully", id);
        materialRepository.deleteById(id);
    }
}

