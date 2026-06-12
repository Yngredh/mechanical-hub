package com.fiap.mechanical_hub.infrastructure.database.mappers;

import com.fiap.mechanical_hub.domain.entities.Stock;
import com.fiap.mechanical_hub.domain.enums.StockStatusEnum;
import com.fiap.mechanical_hub.infrastructure.database.models.StockModel;
import com.fiap.mechanical_hub.mocks.domain.entities.StockMock;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class StockRepositoryMapperTest {

    private static final UUID STOCK_ID = UUID.fromString("00000000-0000-0000-0000-000000000030");
    private static final UUID MATERIAL_ID = UUID.fromString("00000000-0000-0000-0000-000000000020");
    private static final LocalDateTime UPDATED_AT = LocalDateTime.of(2024, 1, 1, 10, 0);

    private StockModel buildModel() {
        return new StockModel(STOCK_ID, MATERIAL_ID, 10, StockStatusEnum.AVAILABLE, UPDATED_AT);
    }

    @Test
    void shouldMapAllFields_whenConvertingJpaEntityToDomainEntity() {
        StockModel model = buildModel();

        Stock stock = StockRepositoryMapper.toDomainEntity(model);

        assertThat(stock.getId()).isEqualTo(model.getId());
        assertThat(stock.getMaterialId()).isEqualTo(model.getMaterialId());
        assertThat(stock.getQuantity()).isEqualTo(model.getQuantity());
        assertThat(stock.getStatus()).isEqualTo(model.getStatus());
        assertThat(stock.getUpdatedAt()).isEqualTo(model.getUpdatedAt());
    }

    @Test
    void shouldMapAllFields_whenConvertingDomainToJpaEntity() {
        Stock stock = StockMock.available(10);

        StockModel model = StockRepositoryMapper.toJpaEntity(stock);

        assertThat(model.getId()).isEqualTo(stock.getId());
        assertThat(model.getMaterialId()).isEqualTo(stock.getMaterialId());
        assertThat(model.getQuantity()).isEqualTo(stock.getQuantity());
        assertThat(model.getStatus()).isEqualTo(stock.getStatus());
        assertThat(model.getUpdatedAt()).isEqualTo(stock.getUpdatedAt());
    }
}
