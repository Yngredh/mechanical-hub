package com.fiap.mechanical_hub.application.usecases.material;

import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.domain.repositories.MaterialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeleteMaterialUseCase {

    private final MaterialRepository repository;

    @Transactional
    public void execute(UUID id) {
        log.info("Iniciando processo de exclusão do material: {}", id);

        if (repository.findById(id).isEmpty()) {
            throw new NotFoundException("Material não encontrado com o id: " + id);
        }

        repository.deleteById(id);
        log.info("Material deletado com sucesso: {}", id);
    }

}

