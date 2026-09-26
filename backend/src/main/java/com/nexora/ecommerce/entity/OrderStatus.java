package com.nexora.ecommerce.entity;

import java.util.Set;

public enum OrderStatus {
    PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED;

    /** Which statuses an order may move to next. */
    public Set<OrderStatus> allowedNext() {
        return switch (this) {
            case PENDING -> Set.of(CONFIRMED, CANCELLED);
            case CONFIRMED -> Set.of(PROCESSING, CANCELLED);
            case PROCESSING -> Set.of(SHIPPED, CANCELLED);
            case SHIPPED -> Set.of(DELIVERED);
            case DELIVERED, CANCELLED -> Set.of();
        };
    }

    public boolean isCancellable() {
        return allowedNext().contains(CANCELLED);
    }
}
