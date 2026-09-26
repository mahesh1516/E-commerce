package com.nexora.ecommerce.util;

import java.util.Set;

public final class AppConstants {

    private AppConstants() {
    }

    public static final int DEFAULT_PAGE_SIZE = 12;
    public static final int MAX_PAGE_SIZE = 50;

    /** Fields the client may sort products by. */
    public static final Set<String> PRODUCT_SORT_FIELDS =
            Set.of("name", "price", "rating", "reviewCount", "createdAt");

    /** SIMULATION: card numbers ending with this are declined. */
    public static final String DECLINED_CARD_SUFFIX = "0000";
}
