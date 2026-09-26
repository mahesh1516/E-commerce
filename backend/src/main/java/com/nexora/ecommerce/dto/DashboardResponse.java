package com.nexora.ecommerce.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record DashboardResponse(
        long totalUsers,
        long totalProducts,
        long totalOrders,
        BigDecimal totalRevenue,
        List<OrderResponse> recentOrders,
        List<InventoryResponse> lowStockProducts,
        Map<String, Long> orderStatusCounts
) {
}
