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

@Service
@RequiredArgsConstructor
@Transactional
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final StockService stockService;
    private final MaterialMapper materialMapper;

    public MaterialResponse create(UpsertMaterialRequest createRequest) {
        Material material = Material.create(
                createRequest.name(),
                createRequest.description(),
                createRequest.unitPrice(),
                createRequest.minStockQuantity()
        );

        Material savedMaterial = materialRepository.save(material);

        return materialMapper.toResponse(savedMaterial);
    }

    @Transactional(readOnly = true)
    public MaterialResponse findById(UUID id) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Material não encontrado para o id: " + id));
        return materialMapper.toResponse(material);
    }

    @Transactional(readOnly = true)
    public List<MaterialResponse> findAll() {
        return materialRepository.findAll().stream()
                .map(materialMapper::toResponse)
                .toList();
    }

    public MaterialResponse update(UUID id, UpsertMaterialRequest updateRequest) {
        Material existingMaterial = materialRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Material não encontrado para o id: " + id));

        existingMaterial.update(
                updateRequest.name(),
                updateRequest.description(),
                updateRequest.unitPrice(),
                updateRequest.minStockQuantity());

        Material savedMaterial = materialRepository.save(existingMaterial);

        return materialMapper.toResponse(savedMaterial);
    }

    public void delete(UUID id) {
        if (materialRepository.findById(id).isEmpty()) {
            throw new NoSuchElementException("Material não encontrado para o id: " + id);
        }
        materialRepository.deleteById(id);
    }
}

