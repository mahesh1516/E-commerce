package com.nexora.ecommerce.service;

import com.nexora.ecommerce.dto.OrderRequest;
import com.nexora.ecommerce.dto.OrderResponse;
import com.nexora.ecommerce.dto.PageResponse;
import com.nexora.ecommerce.entity.OrderStatus;

public interface OrderService {

    // ----- customer -----
    OrderResponse placeOrder(OrderRequest request);

    PageResponse<OrderResponse> getMyOrders(int page, int size);

    OrderResponse getMyOrder(Long id);

    OrderResponse cancelMyOrder(Long id);

    // ----- admin -----
    PageResponse<OrderResponse> getAllOrders(OrderStatus status, int page, int size);

    OrderResponse updateStatus(Long id, OrderStatus newStatus);
}
