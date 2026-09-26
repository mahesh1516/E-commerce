package com.nexora.ecommerce.controller;

import com.nexora.ecommerce.dto.OrderRequest;
import com.nexora.ecommerce.dto.OrderResponse;
import com.nexora.ecommerce.dto.PageResponse;
import com.nexora.ecommerce.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Checkout and order history")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Checkout: place an order from my cart. Card ending 0000 = declined (simulation)")
    public OrderResponse placeOrder(@Valid @RequestBody OrderRequest request) {
        return orderService.placeOrder(request);
    }

    @GetMapping
    @Operation(summary = "My orders (newest first)")
    public PageResponse<OrderResponse> getMyOrders(@RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size) {
        return orderService.getMyOrders(page, size);
    }

    @GetMapping("/{id}")
    @Operation(summary = "My order details")
    public OrderResponse getMyOrder(@PathVariable Long id) {
        return orderService.getMyOrder(id);
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancel my order (only before it ships)")
    public OrderResponse cancel(@PathVariable Long id) {
        return orderService.cancelMyOrder(id);
    }
}
