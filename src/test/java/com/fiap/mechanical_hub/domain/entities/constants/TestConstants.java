package com.fiap.mechanical_hub.domain.entities.constants;

import java.math.BigDecimal;
import java.util.UUID;

public class TestConstants {

    // User Constants
    public static final UUID DEFAULT_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    public static final String DEFAULT_USER_NAME = "John Doe";
    public static final String DEFAULT_USER_EMAIL = "john@example.com";
    public static final String DEFAULT_USER_PASSWORD_HASH = "hashedPassword123";

    // Customer Constants
    public static final UUID DEFAULT_CUSTOMER_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");
    public static final String DEFAULT_CUSTOMER_NAME = "José Silva";
    public static final String DEFAULT_CUSTOMER_DOCUMENT = "52998224725";
    public static final String DEFAULT_CUSTOMER_TELEPHONE = "5511987654321";
    public static final String DEFAULT_CUSTOMER_EMAIL = "customer@example.com";
    public static final String DEFAULT_CUSTOMER_ADDRESS = "Rua Principal, 100";

    // Vehicle Constants
    public static final UUID DEFAULT_VEHICLE_ID = UUID.fromString("00000000-0000-0000-0000-000000000003");
    public static final String DEFAULT_VEHICLE_LICENSE_PLATE = "ABC1D23";
    public static final String DEFAULT_VEHICLE_BRAND = "Toyota";
    public static final String DEFAULT_VEHICLE_MODEL = "Corolla";
    public static final Integer DEFAULT_VEHICLE_YEAR = 2020;
    public static final String DEFAULT_VEHICLE_COLOR = "Branco";

    // Material Constants
    public static final UUID DEFAULT_MATERIAL_ID = UUID.fromString("00000000-0000-0000-0000-000000000004");
    public static final String DEFAULT_MATERIAL_NAME = "Óleo de Motor";
    public static final String DEFAULT_MATERIAL_DESCRIPTION = "Óleo de motor sintético 5W30";
    public static final BigDecimal DEFAULT_MATERIAL_UNIT_PRICE = BigDecimal.valueOf(50.00);
    public static final Integer DEFAULT_MATERIAL_MIN_STOCK = 10;

    // Service Constants
    public static final UUID DEFAULT_SERVICE_ID = UUID.fromString("00000000-0000-0000-0000-000000000005");
    public static final String DEFAULT_SERVICE_NAME = "Troca de óleo";
    public static final String DEFAULT_SERVICE_DESCRIPTION = "Serviço de troca de óleo do motor";
    public static final BigDecimal DEFAULT_SERVICE_LABOR_COST = BigDecimal.valueOf(100.00);
    public static final BigDecimal DEFAULT_SERVICE_BASE_PRICE = BigDecimal.valueOf(150.00);

    // ServiceMaterial Constants
    public static final UUID DEFAULT_SERVICE_MATERIAL_ID = UUID.fromString("00000000-0000-0000-0000-000000000006");
    public static final Integer DEFAULT_SERVICE_MATERIAL_QUANTITY = 5;

    // ServiceOrder Constants
    public static final UUID DEFAULT_SERVICE_ORDER_ID = UUID.fromString("00000000-0000-0000-0000-000000000007");
    public static final String DEFAULT_ORDER_NUMBER = "ORD-001";
    public static final String DEFAULT_REQUEST_DESCRIPTION = "Diagnóstico e reparo da transmissão";
    public static final BigDecimal DEFAULT_ORDER_BUDGET = BigDecimal.valueOf(500.00);

    // OrderTask Constants
    public static final UUID DEFAULT_ORDER_TASK_ID = UUID.fromString("00000000-0000-0000-0000-000000000008");

    // Stock Constants
    public static final UUID DEFAULT_STOCK_ID = UUID.fromString("00000000-0000-0000-0000-000000000009");
    public static final Integer DEFAULT_STOCK_QUANTITY = 50;

    // StockMovement Constants
    public static final UUID DEFAULT_STOCK_MOVEMENT_ID = UUID.fromString("00000000-0000-0000-0000-000000000010");

    // StockPendingItem Constants
    public static final UUID DEFAULT_STOCK_PENDING_ITEM_ID = UUID.fromString("00000000-0000-0000-0000-000000000011");

    // Profile Constants
    public static final String DEFAULT_PROFILE_NAME = "ADMIN";

}

