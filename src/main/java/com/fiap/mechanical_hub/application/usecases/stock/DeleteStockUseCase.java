package com.fiap.mechanical_hub.application.usecases.stock;

import com.fiap.mechanical_hub.application.command.stock.DeleteStockCommand;
import com.fiap.mechanical_hub.domain.entities.Stock;
import com.fiap.mechanical_hub.domain.entities.StockMovement;
import com.fiap.mechanical_hub.domain.enums.StockStatusEnum;
import com.fiap.mechanical_hub.domain.exceptions.BusinessRuleException;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.domain.repositories.MaterialRepository;
import com.fiap.mechanical_hub.domain.repositories.StockMovementRepository;
import com.fiap.mechanical_hub.domain.repositories.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeleteStockUseCase {

    private final StockRepository stockRepository;
    private final StockMovementRepository stockMovementRepository;
    private final MaterialRepository materialRepository;

    @Transactional
    public Stock execute(DeleteStockCommand command) {
        log.info("Starting complete deletion of material and dependencies: {}", command.materialId());

        if (materialRepository.findById(command.materialId()).isEmpty()) {
            throw new NotFoundException("Material não encontrado: " + command.materialId());
        }

        List<Stock> materialStock = stockRepository.findAllByMaterialId(command.materialId());

        if (materialStock.stream().anyMatch(s -> s.getStatus() == StockStatusEnum.RESERVED)) {
            log.warn("Material {} has reserved stock. Deletion not allowed.", command.materialId());
            throw new BusinessRuleException("Não é possível excluir material com estoque reservado.");
        }

        Stock availableStock = stockRepository.findByMaterialIdAndStatus(
            command.materialId(),
            StockStatusEnum.AVAILABLE
        ).orElseThrow(() -> new NotFoundException(
            "Estoque disponível não encontrado para o material: " + command.materialId()
        ));

        Integer availableQuantity = availableStock.getQuantity();

        StockMovement movement = StockMovement.registerDelete(command.materialId(), null, availableQuantity);
        stockMovementRepository.save(movement);

        stockRepository.deleteByMaterialId(command.materialId());
        materialRepository.deleteById(command.materialId());

        log.info("Material and stock deleted successfully: {}", command.materialId());
        return availableStock;
    }
}

