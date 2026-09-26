package com.nexora.ecommerce.service;

import com.nexora.ecommerce.dto.DashboardResponse;
import com.nexora.ecommerce.dto.InventoryResponse;
import com.nexora.ecommerce.dto.InventoryUpdateRequest;
import com.nexora.ecommerce.dto.UserResponse;

import java.util.List;

public interface AdminService {

    DashboardResponse getDashboard();

    List<UserResponse> getAllUsers();

    List<InventoryResponse> getInventory();

    InventoryResponse updateInventory(Long productId, InventoryUpdateRequest request);
}
