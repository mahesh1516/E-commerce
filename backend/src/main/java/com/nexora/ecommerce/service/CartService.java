package com.nexora.ecommerce.service;

import com.nexora.ecommerce.dto.CartItemRequest;
import com.nexora.ecommerce.dto.CartResponse;

public interface CartService {

    CartResponse getCart();

    CartResponse addItem(CartItemRequest request);

    CartResponse updateItem(Long itemId, int quantity);

    CartResponse removeItem(Long itemId);

    CartResponse clearCart();
}
