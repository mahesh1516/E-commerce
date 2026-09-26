package com.nexora.ecommerce.service.impl;

import com.nexora.ecommerce.dto.OrderRequest;
import com.nexora.ecommerce.dto.OrderResponse;
import com.nexora.ecommerce.dto.PageResponse;
import com.nexora.ecommerce.dto.PriceBreakdown;
import com.nexora.ecommerce.entity.*;
import com.nexora.ecommerce.exception.InsufficientStockException;
import com.nexora.ecommerce.exception.InvalidRequestException;
import com.nexora.ecommerce.exception.ResourceNotFoundException;
import com.nexora.ecommerce.mapper.OrderMapper;
import com.nexora.ecommerce.repository.AddressRepository;
import com.nexora.ecommerce.repository.CartRepository;
import com.nexora.ecommerce.repository.OrderRepository;
import com.nexora.ecommerce.service.OrderService;
import com.nexora.ecommerce.service.PaymentService;
import com.nexora.ecommerce.util.AppConstants;
import com.nexora.ecommerce.util.OrderNumberGenerator;
import com.nexora.ecommerce.util.PriceCalculator;
import com.nexora.ecommerce.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final PaymentService paymentService;
    private final PriceCalculator priceCalculator;
    private final SecurityUtils securityUtils;

    /**
     * Places an order. @Transactional = ALL steps succeed or NONE do.
     * If payment fails (or anything throws), stock, order and cart
     * are rolled back automatically.
     */
    @Override
    @Transactional
    public OrderResponse placeOrder(OrderRequest request) {
        // 1. Validate user
        User user = securityUtils.getCurrentUser();

        Address address = addressRepository.findByIdAndUserId(request.addressId(), user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Address", request.addressId()));

        // 2. Validate cart
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new InvalidRequestException("Your cart is empty"));
        if (cart.getItems().isEmpty()) {
            throw new InvalidRequestException("Your cart is empty");
        }

        // 3. Validate stock (again - it may have changed since adding to cart)
        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            if (!product.isActive()) {
                throw new InvalidRequestException(product.getName() + " is no longer available");
            }
            if (product.getStock() < item.getQuantity()) {
                throw new InsufficientStockException("Only " + product.getStock() + " unit(s) of "
                        + product.getName() + " left. Please update your cart.");
            }
        }

        // 4. Calculate totals
        List<PriceCalculator.Line> lines = cart.getItems().stream()
                .map(i -> new PriceCalculator.Line(i.getProduct().getPrice(),
                        i.getProduct().getEffectivePrice(), i.getQuantity()))
                .toList();
        PriceBreakdown totals = priceCalculator.calculate(lines);

        // 5. Create order (with address snapshot)
        Order order = new Order();
        order.setOrderNumber(OrderNumberGenerator.next());
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setSubtotal(totals.subtotal());
        order.setDiscount(totals.discount());
        order.setTax(totals.tax());
        order.setShippingFee(totals.shipping());
        order.setGrandTotal(totals.grandTotal());
        copyAddress(address, order);

        // 6. Create order items + 7. reduce stock
        for (CartItem ci : cart.getItems()) {
            Product p = ci.getProduct();
            BigDecimal unit = p.getEffectivePrice();

            OrderItem oi = new OrderItem();
            oi.setProduct(p);
            oi.setProductName(p.getName());
            oi.setImageUrl(p.getImageUrl());
            oi.setUnitPrice(p.getPrice());
            oi.setFinalUnitPrice(unit);
            oi.setQuantity(ci.getQuantity());
            oi.setLineTotal(unit.multiply(BigDecimal.valueOf(ci.getQuantity())));
            order.addItem(oi);

            Inventory inventory = p.getInventory();
            inventory.setQuantity(inventory.getQuantity() - ci.getQuantity());
        }

        // 8. Payment (throws PaymentFailedException -> full rollback)
        Payment payment = paymentService.processPayment(order, request.paymentMethod(), request.cardNumber());
        order.setStatus(OrderStatus.CONFIRMED);

        orderRepository.save(order); // cascades items + payment

        // 9. Clear cart
        cart.getItems().clear();

        log.info("Order placed: {} user={} total={} payment={}",
                order.getOrderNumber(), user.getId(), order.getGrandTotal(), payment.getStatus());

        // 10. Return confirmation
        return OrderMapper.toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> getMyOrders(int page, int size) {
        Long userId = securityUtils.getCurrentUser().getId();
        Page<OrderResponse> result = orderRepository
                .findByUserId(userId, pageable(page, size))
                .map(OrderMapper::toResponse);
        return PageResponse.from(result);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getMyOrder(Long id) {
        return OrderMapper.toResponse(findOwn(id));
    }

    @Override
    @Transactional
    public OrderResponse cancelMyOrder(Long id) {
        Order order = findOwn(id);
        cancel(order);
        log.info("Order cancelled by customer: {}", order.getOrderNumber());
        return OrderMapper.toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> getAllOrders(OrderStatus status, int page, int size) {
        Page<Order> orders = status == null
                ? orderRepository.findAll(pageable(page, size))
                : orderRepository.findByStatus(status, pageable(page, size));
        return PageResponse.from(orders.map(OrderMapper::toResponse));
    }

    @Override
    @Transactional
    public OrderResponse updateStatus(Long id, OrderStatus newStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
        OrderStatus current = order.getStatus();

        if (current == newStatus) {
            return OrderMapper.toResponse(order);
        }
        if (!current.allowedNext().contains(newStatus)) {
            throw new InvalidRequestException("Cannot change order status from " + current + " to " + newStatus);
        }

        if (newStatus == OrderStatus.CANCELLED) {
            cancel(order);
        } else {
            order.setStatus(newStatus);
            // Cash on delivery is "paid" when delivered
            Payment payment = order.getPayment();
            if (newStatus == OrderStatus.DELIVERED && payment != null
                    && payment.getMethod() == PaymentMethod.COD
                    && payment.getStatus() == PaymentStatus.PENDING) {
                payment.setStatus(PaymentStatus.SUCCESS);
                payment.setTransactionId(PaymentServiceImpl.newTransactionId());
            }
        }
        log.info("Order {} status {} -> {}", order.getOrderNumber(), current, newStatus);
        return OrderMapper.toResponse(order);
    }

    // ------------------------------------------------------------

    /** Cancels an order: restores stock and refunds a successful payment. */
    private void cancel(Order order) {
        if (!order.getStatus().isCancellable()) {
            throw new InvalidRequestException("Order " + order.getOrderNumber()
                    + " can no longer be cancelled (status: " + order.getStatus() + ")");
        }
        for (OrderItem item : order.getItems()) {
            Inventory inventory = item.getProduct().getInventory();
            inventory.setQuantity(inventory.getQuantity() + item.getQuantity());
        }
        order.setStatus(OrderStatus.CANCELLED);

        Payment payment = order.getPayment();
        if (payment != null && payment.getStatus() == PaymentStatus.SUCCESS) {
            payment.setStatus(PaymentStatus.REFUNDED);
        }
    }

    private Order findOwn(Long id) {
        Long userId = securityUtils.getCurrentUser().getId();
        return orderRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
    }

    private static PageRequest pageable(int page, int size) {
        int safeSize = Math.min(Math.max(size, 1), AppConstants.MAX_PAGE_SIZE);
        return PageRequest.of(Math.max(page, 0), safeSize, Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    private static void copyAddress(Address a, Order o) {
        o.setShippingName(a.getName());
        o.setShippingPhone(a.getPhone());
        o.setShippingLine1(a.getAddressLine1());
        o.setShippingLine2(a.getAddressLine2());
        o.setShippingCity(a.getCity());
        o.setShippingState(a.getState());
        o.setShippingPostalCode(a.getPostalCode());
        o.setShippingCountry(a.getCountry());
    }
}
