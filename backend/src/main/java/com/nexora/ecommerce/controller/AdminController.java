package com.nexora.ecommerce.controller;

import com.nexora.ecommerce.dto.*;
import com.nexora.ecommerce.entity.OrderStatus;
import com.nexora.ecommerce.service.AdminService;
import com.nexora.ecommerce.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Protected twice: URL rule in SecurityConfig + @PreAuthorize here. */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final AdminService adminService;
    private final OrderService orderService;

    @GetMapping("/dashboard")
    @Operation(summary = "Totals, revenue, recent orders, low stock")
    public DashboardResponse dashboard() {
        return adminService.getDashboard();
    }

    @GetMapping("/users")
    @Operation(summary = "All users")
    public List<UserResponse> users() {
        return adminService.getAllUsers();
    }

    @GetMapping("/orders")
    @Operation(summary = "All orders, optional status filter")
    public PageResponse<OrderResponse> orders(@RequestParam(required = false) OrderStatus status,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "20") int size) {
        return orderService.getAllOrders(status, page, size);
    }

    @PutMapping("/orders/{id}/status")
    @Operation(summary = "Update order status (validated transitions)")
    public OrderResponse updateStatus(@PathVariable Long id,
                                      @Valid @RequestBody OrderStatusUpdateRequest request) {
        return orderService.updateStatus(id, request.status());
    }

    @GetMapping("/inventory")
    @Operation(summary = "Stock levels for all active products")
    public List<InventoryResponse> inventory() {
        return adminService.getInventory();
    }

    @PutMapping("/inventory/{productId}")
    @Operation(summary = "Set stock quantity / low-stock threshold")
    public InventoryResponse updateInventory(@PathVariable Long productId,
                                             @Valid @RequestBody InventoryUpdateRequest request) {
        return adminService.updateInventory(productId, request);
    }
}
