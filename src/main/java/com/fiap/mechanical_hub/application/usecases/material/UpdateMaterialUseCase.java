package com.fiap.mechanical_hub.application.usecases.material;

import com.fiap.mechanical_hub.application.command.material.UpdateMaterialCommand;
import com.fiap.mechanical_hub.application.dto.material.MaterialResponse;
import com.fiap.mechanical_hub.domain.entities.Material;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.domain.repositories.MaterialRepository;
import com.fiap.mechanical_hub.application.mappers.MaterialMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UpdateMaterialUseCase {

    private final MaterialRepository repository;

    @Transactional
    public MaterialResponse execute(UpdateMaterialCommand command) {
        log.info("Updating material with id: {}", command.id());

        Material existingMaterial = repository.findById(command.id())
                .orElseThrow(() -> new NotFoundException("Material não encontrado para o id: " + command.id()));

        existingMaterial.update(
                command.name(),
                command.description(),
                command.unitPrice(),
                command.minStockQuantity()
        );

        Material savedMaterial = repository.save(existingMaterial);
        log.info("Material updated with id: {}", savedMaterial.getId());

        return MaterialMapper.toResponse(savedMaterial);
    }

}

