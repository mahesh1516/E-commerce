package com.nexora.ecommerce.service.impl;

import com.nexora.ecommerce.dto.*;
import com.nexora.ecommerce.entity.Inventory;
import com.nexora.ecommerce.entity.OrderStatus;
import com.nexora.ecommerce.entity.PaymentStatus;
import com.nexora.ecommerce.exception.ResourceNotFoundException;
import com.nexora.ecommerce.mapper.InventoryMapper;
import com.nexora.ecommerce.mapper.OrderMapper;
import com.nexora.ecommerce.mapper.UserMapper;
import com.nexora.ecommerce.repository.InventoryRepository;
import com.nexora.ecommerce.repository.OrderRepository;
import com.nexora.ecommerce.repository.ProductRepository;
import com.nexora.ecommerce.repository.UserRepository;
import com.nexora.ecommerce.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private static final Logger log = LoggerFactory.getLogger(AdminServiceImpl.class);

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final InventoryRepository inventoryRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardResponse getDashboard() {
        BigDecimal revenue = orderRepository.sumRevenue(OrderStatus.CANCELLED, PaymentStatus.SUCCESS);
        if (revenue == null) {
            revenue = BigDecimal.ZERO;
        }

        Map<String, Long> statusCounts = new LinkedHashMap<>();
        for (OrderStatus s : OrderStatus.values()) {
            statusCounts.put(s.name(), 0L);
        }
        for (Object[] row : orderRepository.countByStatus()) {
            statusCounts.put(((OrderStatus) row[0]).name(), (Long) row[1]);
        }

        return new DashboardResponse(
                userRepository.count(),
                productRepository.countByActiveTrue(),
                orderRepository.count(),
                revenue,
                orderRepository.findTop5ByOrderByCreatedAtDesc().stream().map(OrderMapper::toResponse).toList(),
                inventoryRepository.findLowStock().stream().map(InventoryMapper::toResponse).toList(),
                statusCounts);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")).stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponse> getInventory() {
        return inventoryRepository.findAllActive().stream()
                .map(InventoryMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public InventoryResponse updateInventory(Long productId, InventoryUpdateRequest request) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory for product", productId));
        inventory.setQuantity(request.quantity());
        if (request.lowStockThreshold() != null) {
            inventory.setLowStockThreshold(request.lowStockThreshold());
        }
        log.info("Inventory updated: product={} quantity={}", productId, request.quantity());
        return InventoryMapper.toResponse(inventory);
    }
}
