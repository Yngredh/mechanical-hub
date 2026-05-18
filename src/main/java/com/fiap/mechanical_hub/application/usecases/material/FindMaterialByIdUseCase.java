package com.fiap.mechanical_hub.application.usecases.material;

import com.fiap.mechanical_hub.application.command.material.FindMaterialByIdCommand;
import com.fiap.mechanical_hub.application.dto.material.MaterialResponse;
import com.fiap.mechanical_hub.domain.entities.Material;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.domain.repositories.MaterialRepository;
import com.fiap.mechanical_hub.infrastructure.http.mappers.MaterialHttpMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class FindMaterialByIdUseCase {

    private static final String MATERIAL_NOT_FOUND_MESSAGE = "Material não encontrado para o id: ";
    private final MaterialRepository repository;

    @Transactional(readOnly = true)
    public MaterialResponse execute(UUID id) {
        log.info("Finding material with id: {}", id);
        Material material = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(MATERIAL_NOT_FOUND_MESSAGE + id));
        log.info("Material found: {}", material.getName());
        return MaterialHttpMapper.toResponse(material);
    }

}

