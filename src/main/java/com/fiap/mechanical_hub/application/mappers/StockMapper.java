package com.fiap.mechanical_hub.application.mappers;

import com.fiap.mechanical_hub.application.dto.stock.StockSummaryResponse;
import com.fiap.mechanical_hub.domain.repositories.MaterialRepository;
import com.fiap.mechanical_hub.domain.entities.Stock;
import com.fiap.mechanical_hub.domain.enums.StockStatusEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class StockMapper {

    private final MaterialRepository materialRepository;

    public List<StockSummaryResponse> buildStockSummary(List<Stock> stock) {
        Map<UUID, Map<String, Integer>> aggregatedByMaterial = stock.stream()
                .collect(Collectors.groupingBy(
                        Stock::getMaterialId,
                        Collectors.toMap(
                                s -> s.getStatus().name(),
                                Stock::getQuantity,
                                Integer::sum
                        )
                ));

        return aggregatedByMaterial.entrySet().stream()
                .map(entry -> {
                    UUID materialId = entry.getKey();
                    Map<String, Integer> statusMap = entry.getValue();

                    int quantityAvailable = statusMap.getOrDefault(StockStatusEnum.AVAILABLE.name(), 0);
                    int quantityReserved = statusMap.getOrDefault(StockStatusEnum.RESERVED.name(), 0);
                    int quantityTotal = quantityAvailable + quantityReserved;

                    String materialName = materialRepository.findById(materialId)
                            .orElseThrow(() -> new NoSuchElementException("Material não encontrado")).getName();

                    return new StockSummaryResponse(
                            materialId,
                            materialName,
                            quantityTotal,
                            quantityAvailable,
                            quantityReserved
                    );
                }).toList();
    }
}
