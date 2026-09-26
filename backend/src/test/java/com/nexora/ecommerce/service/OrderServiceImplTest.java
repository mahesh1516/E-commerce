package com.nexora.ecommerce.service;

import com.nexora.ecommerce.dto.OrderRequest;
import com.nexora.ecommerce.dto.OrderResponse;
import com.nexora.ecommerce.entity.*;
import com.nexora.ecommerce.exception.InsufficientStockException;
import com.nexora.ecommerce.exception.InvalidRequestException;
import com.nexora.ecommerce.exception.PaymentFailedException;
import com.nexora.ecommerce.repository.AddressRepository;
import com.nexora.ecommerce.repository.CartRepository;
import com.nexora.ecommerce.repository.OrderRepository;
import com.nexora.ecommerce.service.impl.OrderServiceImpl;
import com.nexora.ecommerce.util.PriceCalculator;
import com.nexora.ecommerce.util.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    private static final String GOOD_CARD = "4111111111111111";

    @Mock private OrderRepository orderRepository;
    @Mock private CartRepository cartRepository;
    @Mock private AddressRepository addressRepository;
    @Mock private PaymentService paymentService;
    @Mock private SecurityUtils securityUtils;

    @Spy private PriceCalculator priceCalculator =
            new PriceCalculator(new BigDecimal("0.10"), new BigDecimal("50"), new BigDecimal("5000"));

    @InjectMocks private OrderServiceImpl orderService;

    private User user;
    private Cart cart;

    @BeforeEach
    void setUp() {
        user = TestData.user();
        cart = TestData.cart(user);
        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(addressRepository.findByIdAndUserId(5L, 1L)).thenReturn(Optional.of(TestData.address(user)));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
    }

    private OrderRequest cardRequest(String card) {
        return new OrderRequest(5L, PaymentMethod.CARD, card, "Mahesh");
    }

    @Test
    void placeOrder_success_reducesStock_clearsCart_confirmsOrder() {
        Product product = TestData.product(10L, 5);
        TestData.cartItem(cart, product, 2);

        when(paymentService.processPayment(any(Order.class), eq(PaymentMethod.CARD), eq(GOOD_CARD)))
                .thenAnswer(inv -> {
                    Order order = inv.getArgument(0);
                    Payment p = new Payment();
                    p.setOrder(order);
                    p.setAmount(order.getGrandTotal());
                    p.setMethod(PaymentMethod.CARD);
                    p.setStatus(PaymentStatus.SUCCESS);
                    order.setPayment(p);
                    return p;
                });
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderResponse response = orderService.placeOrder(cardRequest(GOOD_CARD));

        assertThat(response.status()).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(response.grandTotal()).isEqualByComparingTo("2030");
        assertThat(response.items()).hasSize(1);
        assertThat(response.payment().status()).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(product.getInventory().getQuantity()).isEqualTo(3); // 5 - 2
        assertThat(cart.getItems()).isEmpty();
    }

    @Test
    void placeOrder_emptyCart_throws() {
        assertThrows(InvalidRequestException.class, () -> orderService.placeOrder(cardRequest(GOOD_CARD)));
        verify(orderRepository, never()).save(any());
    }

    @Test
    void placeOrder_notEnoughStock_throws() {
        TestData.cartItem(cart, TestData.product(10L, 1), 3);

        assertThrows(InsufficientStockException.class, () -> orderService.placeOrder(cardRequest(GOOD_CARD)));
        verify(orderRepository, never()).save(any());
        verifyNoInteractions(paymentService);
    }

    @Test
    void placeOrder_paymentDeclined_throws_andOrderIsNotSaved() {
        TestData.cartItem(cart, TestData.product(10L, 5), 1);
        when(paymentService.processPayment(any(Order.class), eq(PaymentMethod.CARD), eq("4111111111110000")))
                .thenThrow(new PaymentFailedException("declined"));

        assertThrows(PaymentFailedException.class,
                () -> orderService.placeOrder(cardRequest("4111111111110000")));
        // In the real app @Transactional rolls back the stock change too
        verify(orderRepository, never()).save(any());
        assertThat(cart.getItems()).hasSize(1);
    }
}
