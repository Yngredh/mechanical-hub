package com.fiap.mechanical_hub.application.usecases.material;

import com.fiap.mechanical_hub.domain.entities.Material;
import com.fiap.mechanical_hub.domain.entities.ServiceData;
import com.fiap.mechanical_hub.domain.entities.ServiceMaterial;
import com.fiap.mechanical_hub.domain.entities.StockPendingItem;
import com.fiap.mechanical_hub.domain.exceptions.BusinessRuleException;
import com.fiap.mechanical_hub.domain.exceptions.MaterialNotFoundException;
import com.fiap.mechanical_hub.domain.repositories.MaterialRepository;
import com.fiap.mechanical_hub.domain.repositories.ServiceMaterialRepository;
import com.fiap.mechanical_hub.domain.repositories.ServiceRepository;
import com.fiap.mechanical_hub.domain.repositories.StockPendingItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeleteMaterialUseCase {

    private final MaterialRepository materialRepository;
    private final ServiceMaterialRepository serviceMaterialRepository;
    private final ServiceRepository serviceRepository;
    private final StockPendingItemRepository stockPendingItemRepository;

    @Transactional
    public void execute(UUID id) {
        log.info("Iniciando processo de exclusão do material: {}", id);

        Material material = materialRepository.findById(id)
            .orElseThrow(() -> new MaterialNotFoundException(id.toString()));

        List<StockPendingItem> materialPendency = stockPendingItemRepository.findByMaterialIdOrderByCreatedAtAsc(id);

        if (!materialPendency.isEmpty()) {
             throw new BusinessRuleException("Não é possível deletar o material, existem pendências de estoque associadas a ele.");
        }

        List<ServiceMaterial> serviceMaterials = serviceMaterialRepository.findByMaterialId(id);

        List<UUID> serviceIds = serviceMaterials.stream().map(ServiceMaterial::getServiceId).toList();

        List<ServiceData> services = serviceRepository.findAllIn(serviceIds);

        if (services.stream().anyMatch(ServiceData::isActive)) {
            throw new BusinessRuleException("Não é possível deletar o material, existem serviços ativos associados a ele.");
        }

        material.deactivate();

        materialRepository.save(material);

        log.info("Material deletado com sucesso: {}", id);
    }

}

