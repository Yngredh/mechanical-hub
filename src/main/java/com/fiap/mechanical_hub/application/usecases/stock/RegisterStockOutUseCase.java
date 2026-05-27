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
public class RegisterStockOutUseCase {

    private final StockRepository stockRepository;
    private final StockMovementRepository stockMovementRepository;

    @Transactional
    public Stock execute(UUID serviceOrderId, OrderTask orderTask) {
        log.info("Registering stock out for service order: {}", serviceOrderId);

        Stock lastProcessedStock = null;

        List<ServiceMaterial> materials = orderTask.getServiceData().getMaterials();
        for (ServiceMaterial serviceMaterial : materials) {
            UUID materialId = serviceMaterial.getMaterial().getId();
            Integer quantity = serviceMaterial.getQuantity();

            Stock stock = stockRepository.findByMaterialIdAndStatus(
                materialId, StockStatusEnum.RESERVED
            ).orElseThrow(() -> new NotFoundException(
                "Estoque reservado não encontrado para o material: " + materialId
            ));

            stock.decreaseReserved(quantity);
            Stock savedStock = stockRepository.save(stock);

            StockMovement movement = StockMovement.registerStockOut(materialId, serviceOrderId, quantity);
            stockMovementRepository.save(movement);

            log.info("Stock out registered: {} units of material {} from order {}", quantity, materialId, serviceOrderId);
            lastProcessedStock = savedStock;
        }

        return lastProcessedStock;
    }
}

