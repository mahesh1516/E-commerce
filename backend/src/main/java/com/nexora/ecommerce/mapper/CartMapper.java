package com.nexora.ecommerce.mapper;

import com.nexora.ecommerce.dto.CartItemResponse;
import com.nexora.ecommerce.dto.CartResponse;
import com.nexora.ecommerce.dto.PriceBreakdown;
import com.nexora.ecommerce.entity.Cart;
import com.nexora.ecommerce.entity.CartItem;
import com.nexora.ecommerce.entity.Product;

import java.math.BigDecimal;
import java.util.List;

public final class CartMapper {

    private CartMapper() {
    }

    public static CartResponse toResponse(Cart cart, PriceBreakdown totals) {
        List<CartItemResponse> items = cart.getItems().stream().map(CartMapper::toItem).toList();
        int totalItems = cart.getItems().stream().mapToInt(CartItem::getQuantity).sum();

        return new CartResponse(
                cart.getId(),
                items,
                totalItems,
                totals.subtotal(),
                totals.discount(),
                totals.tax(),
                totals.shipping(),
                totals.grandTotal());
    }

    private static CartItemResponse toItem(CartItem item) {
        Product p = item.getProduct();
        BigDecimal unit = p.getEffectivePrice();
        return new CartItemResponse(
                item.getId(),
                p.getId(),
                p.getName(),
                p.getBrand(),
                p.getImageUrl(),
                p.getPrice(),
                unit,
                item.getQuantity(),
                unit.multiply(BigDecimal.valueOf(item.getQuantity())),
                p.getStock());
    }
}
