package com.fiap.mechanical_hub.application.usecases.stock;

import com.fiap.mechanical_hub.application.command.stock.RegisterStockEntryCommand;
import com.fiap.mechanical_hub.domain.entities.Stock;
import com.fiap.mechanical_hub.domain.entities.StockMovement;
import com.fiap.mechanical_hub.domain.enums.StockStatusEnum;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.domain.repositories.StockMovementRepository;
import com.fiap.mechanical_hub.domain.repositories.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class RegisterStockEntryUseCase {

    private final StockRepository stockRepository;
    private final StockMovementRepository stockMovementRepository;
    private final ResolveMaterialPendingItemsUseCase resolveMaterialPendingItemsUseCase;

    @Transactional
    public Stock execute(RegisterStockEntryCommand command) {
        log.info("Registering stock entry for material ID: {}", command.materialId());

        Stock updatedStock = registerMaterialEntry(command);
        resolveMaterialPendingItemsUseCase.execute(command.materialId(), updatedStock);

        log.info("Stock entry registration completed for material ID: {}", command.materialId());
        return updatedStock;
    }

    private Stock registerMaterialEntry(RegisterStockEntryCommand command) {
        Stock stock = stockRepository.findByMaterialIdAndStatus(
            command.materialId(),
            StockStatusEnum.AVAILABLE
        ).orElseThrow(() -> {
            log.error("Stock not found for material ID: {}", command.materialId());
            return new NotFoundException("Estoque não encontrado para o material id: " + command.materialId());
        });

        stock.addQuantity(command.quantity());
        Stock updatedStock = stockRepository.save(stock);

        StockMovement movement = StockMovement.buildStockEntryMovement(command.materialId(), command.quantity());
        stockMovementRepository.save(movement);

        return updatedStock;
    }
}

