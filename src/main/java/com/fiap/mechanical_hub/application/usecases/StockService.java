package com.fiap.mechanical_hub.application.usecases;

import com.fiap.mechanical_hub.application.dto.stock.StockEntryItem;
import com.fiap.mechanical_hub.domain.entities.Stock;
import com.fiap.mechanical_hub.domain.enums.StockStatusEnum;
import com.fiap.mechanical_hub.domain.exceptions.NotFoundException;
import com.fiap.mechanical_hub.domain.repositories.MaterialRepository;
import com.fiap.mechanical_hub.domain.repositories.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class StockService {
    private final StockMovementUseCase stockMovementUseCase;
    private final MaterialRepository materialRepository;
    private final StockRepository stockRepository;

    @Transactional
    public void setStockForNewMaterial(UUID materialId) {
        log.info("Setting stock for new material with ID: {}", materialId);
        Stock stock = Stock.setStockForNewMaterial(materialId);
        stockRepository.save(stock);
    }

    @Transactional
    public void registerStockEntry(List<StockEntryItem> items) {
        log.info("Registering stock entry");

        for (StockEntryItem item : items) {
            materialRepository.findById(item.materialId())
                    .orElseThrow(() -> {
                        log.error("Material not found with ID: {}", item.materialId());
                        return new NotFoundException(
                                "Material não encontrado para o id: " + item.materialId()
                        );
                    });

            Stock stock = stockRepository.findByMaterialIdAndStatus(item.materialId(), StockStatusEnum.AVAILABLE)
                    .orElseThrow(() -> {
                        log.error("Stock not found for material ID: {}", item.materialId());
                        return new NotFoundException(
                                "Estoque não encontrado para o material id: " + item.materialId()
                        );
                    });
            stock.addQuantity(item.quantity());

            stockRepository.save(stock);

            stockMovementUseCase.registerStockEntryMovement(item);

            // TODO: Acionar StockPendingResolver.resolveForMaterial(item.materialId())
        }

        log.info("Stock entry registration completed");
    }
}
