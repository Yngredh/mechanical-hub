package com.fiap.mechanical_hub.infrastructure.database.mappers;

import com.fiap.mechanical_hub.domain.entities.StockPendingItem;
import com.fiap.mechanical_hub.infrastructure.database.models.StockPendingItemModel;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class StockPendingItemRepositoryMapperTest {

    private static final UUID ITEM_ID = UUID.fromString("00000000-0000-0000-0000-000000000090");
    private static final UUID SERVICE_ORDER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID MATERIAL_ID = UUID.fromString("00000000-0000-0000-0000-000000000020");
    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2024, 1, 1, 10, 0);

    private StockPendingItem buildDomain() {
        return new StockPendingItem(ITEM_ID, SERVICE_ORDER_ID, MATERIAL_ID, 3, CREATED_AT);
    }

    private StockPendingItemModel buildModel() {
        return new StockPendingItemModel(ITEM_ID, SERVICE_ORDER_ID, MATERIAL_ID, 3, CREATED_AT);
    }

    @Test
    void shouldMapAllFields_whenConvertingDomainToModel() {
        StockPendingItem domain = buildDomain();

        StockPendingItemModel model = StockPendingItemRepositoryMapper.toModel(domain);

        assertThat(model.getId()).isEqualTo(domain.getId());
        assertThat(model.getServiceOrderId()).isEqualTo(domain.getServiceOrderId());
        assertThat(model.getMaterialId()).isEqualTo(domain.getMaterialId());
        assertThat(model.getQuantity()).isEqualTo(domain.getQuantity());
        assertThat(model.getCreatedAt()).isEqualTo(domain.getCreatedAt());
    }

    @Test
    void shouldMapAllFields_whenConvertingModelToEntity() {
        StockPendingItemModel model = buildModel();

        StockPendingItem domain = StockPendingItemRepositoryMapper.toEntity(model);

        assertThat(domain.getId()).isEqualTo(model.getId());
        assertThat(domain.getServiceOrderId()).isEqualTo(model.getServiceOrderId());
        assertThat(domain.getMaterialId()).isEqualTo(model.getMaterialId());
        assertThat(domain.getQuantity()).isEqualTo(model.getQuantity());
        assertThat(domain.getCreatedAt()).isEqualTo(model.getCreatedAt());
    }
}
