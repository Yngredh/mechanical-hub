package com.fiap.mechanical_hub.application.usecases;

import com.fiap.mechanical_hub.application.dto.stock.StockEntryItem;
import com.fiap.mechanical_hub.domain.entities.StockMovement;
import com.fiap.mechanical_hub.domain.repositories.StockMovementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class StockMovementUseCase {

    private final StockMovementRepository stockMovementRepository;

    public void registerStockEntryMovement(StockEntryItem item) {
        StockMovement movement = StockMovement.buildStockEntryMovement(item.materialId(), item.quantity());
        StockMovement register = stockMovementRepository.save(movement);
        log.info("Stock entry movement registered: materialId {}, quantity {}, movementType {}",
                register.getMaterialId(), register.getQuantity(), register.getMovementType());
    }
}
