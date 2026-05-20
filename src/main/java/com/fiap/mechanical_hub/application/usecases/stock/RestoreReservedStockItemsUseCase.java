package com.fiap.mechanical_hub.application.usecases.stock;

import com.fiap.mechanical_hub.domain.entities.OrderTask;
import com.fiap.mechanical_hub.domain.entities.ServiceMaterial;
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

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class RestoreReservedStockItemsUseCase {

    private final StockRepository stockRepository;
    private final StockMovementRepository stockMovementRepository;

    @Transactional
    public Stock execute(UUID serviceOrderId, List<OrderTask> orderTasks) {
        log.info("Restoring reserved stock items for order {}", serviceOrderId);

        Stock lastProcessedStock = null;

        for (OrderTask task : orderTasks) {
            List<ServiceMaterial> materials = task.getServiceData().getMaterials();

            for (ServiceMaterial sm : materials) {
                UUID materialId = sm.getMaterial().getId();
                int quantity = sm.getQuantity();

                Stock reservedStock = stockRepository.findByMaterialIdAndStatus(
                    materialId, StockStatusEnum.RESERVED
                ).orElseThrow(() -> new NotFoundException(
                    "Estoque reservado não encontrado para o material: " + materialId
                ));

                reservedStock.release(quantity);
                stockRepository.save(reservedStock);

                Stock availableStock = stockRepository.findByMaterialIdAndStatus(
                    materialId, StockStatusEnum.AVAILABLE
                ).orElseThrow(() -> new NotFoundException(
                    "Estoque disponível não encontrado para o material: " + materialId
                ));

                availableStock.replenish(quantity);
                Stock savedAvailableStock = stockRepository.save(availableStock);

                StockMovement movement = StockMovement.registerReturn(materialId, serviceOrderId, quantity);
                stockMovementRepository.save(movement);

                log.info("Restored {} units of material {} from order {}", quantity, materialId, serviceOrderId);
                lastProcessedStock = savedAvailableStock;
            }
        }

        return lastProcessedStock;
    }
}

