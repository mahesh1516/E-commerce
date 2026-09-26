package com.nexora.ecommerce.mapper;

import com.nexora.ecommerce.dto.OrderItemResponse;
import com.nexora.ecommerce.dto.OrderResponse;
import com.nexora.ecommerce.dto.PaymentResponse;
import com.nexora.ecommerce.dto.ShippingAddressResponse;
import com.nexora.ecommerce.entity.Order;
import com.nexora.ecommerce.entity.OrderItem;
import com.nexora.ecommerce.entity.Payment;

import java.util.List;

public final class OrderMapper {

    private OrderMapper() {
    }

    public static OrderResponse toResponse(Order o) {
        List<OrderItemResponse> items = o.getItems().stream().map(OrderMapper::toItem).toList();
        int totalItems = o.getItems().stream().mapToInt(OrderItem::getQuantity).sum();

        ShippingAddressResponse address = new ShippingAddressResponse(
                o.getShippingName(), o.getShippingPhone(), o.getShippingLine1(), o.getShippingLine2(),
                o.getShippingCity(), o.getShippingState(), o.getShippingPostalCode(), o.getShippingCountry());

        return new OrderResponse(
                o.getId(),
                o.getOrderNumber(),
                o.getStatus(),
                o.getStatus().isCancellable(),
                o.getSubtotal(),
                o.getDiscount(),
                o.getTax(),
                o.getShippingFee(),
                o.getGrandTotal(),
                totalItems,
                address,
                items,
                toPayment(o.getPayment()),
                o.getUser().getName(),
                o.getUser().getEmail(),
                o.getCreatedAt(),
                o.getUpdatedAt());
    }

    private static OrderItemResponse toItem(OrderItem i) {
        return new OrderItemResponse(i.getId(), i.getProduct().getId(), i.getProductName(),
                i.getImageUrl(), i.getUnitPrice(), i.getFinalUnitPrice(), i.getQuantity(), i.getLineTotal());
    }

    private static PaymentResponse toPayment(Payment p) {
        if (p == null) {
            return null;
        }
        return new PaymentResponse(p.getId(), p.getAmount(), p.getMethod(), p.getStatus(),
                p.getTransactionId(), p.getCardLast4(), p.getCreatedAt());
    }
}
