package com.fiap.mechanical_hub.application.usecases;

import com.fiap.mechanical_hub.application.dto.stock.StockEntryRequest;
import com.fiap.mechanical_hub.application.repositories.StockMovementRepository;
import com.fiap.mechanical_hub.domain.entities.StockMovement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class StockMovementUseCase {

    private final StockMovementRepository stockMovementRepository;

    public void registerStockEntryMovement(StockEntryRequest item) {
        StockMovement movement = StockMovement.buildStockEntryMovement(item.materialId(), item.quantity());
        StockMovement register = stockMovementRepository.save(movement);
        log.info("Stock entry movement registered: materialId {}, quantity {}, movementType {}",
                register.getMaterialId(), register.getQuantity(), register.getMovementType());
    }

    public void registerStockReturnMovement(UUID materialId, UUID serviceOrderId, Integer quantity) {
        StockMovement movement = StockMovement.registerReturn(materialId, serviceOrderId, quantity);
        stockMovementRepository.save(movement);
    }

    public void registerStockDeleteMovement(UUID materialId, UUID serviceOrderId, Integer quantity) {
        StockMovement movement = StockMovement.registerDelete(materialId, serviceOrderId, quantity);
        stockMovementRepository.save(movement);
    }

    public void registerStockOutMovement(UUID materialId, UUID serviceOrderId, Integer quantity) {
        StockMovement movement = StockMovement.registerStockOut(materialId, serviceOrderId, quantity);
        stockMovementRepository.save(movement);
    }
}
