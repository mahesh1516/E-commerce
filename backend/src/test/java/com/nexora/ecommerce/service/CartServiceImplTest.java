package com.nexora.ecommerce.service;

import com.nexora.ecommerce.dto.CartItemRequest;
import com.nexora.ecommerce.dto.CartResponse;
import com.nexora.ecommerce.entity.Cart;
import com.nexora.ecommerce.entity.CartItem;
import com.nexora.ecommerce.entity.Product;
import com.nexora.ecommerce.entity.User;
import com.nexora.ecommerce.exception.InsufficientStockException;
import com.nexora.ecommerce.exception.ResourceNotFoundException;
import com.nexora.ecommerce.repository.CartItemRepository;
import com.nexora.ecommerce.repository.CartRepository;
import com.nexora.ecommerce.repository.ProductRepository;
import com.nexora.ecommerce.service.impl.CartServiceImpl;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock private CartRepository cartRepository;
    @Mock private CartItemRepository cartItemRepository;
    @Mock private ProductRepository productRepository;
    @Mock private SecurityUtils securityUtils;

    // Real calculator (spy) so totals are really computed
    @Spy private PriceCalculator priceCalculator =
            new PriceCalculator(new BigDecimal("0.10"), new BigDecimal("50"), new BigDecimal("5000"));

    @InjectMocks private CartServiceImpl cartService;

    private User user;
    private Cart cart;

    @BeforeEach
    void setUp() {
        user = TestData.user();
        cart = TestData.cart(user);
        when(securityUtils.getCurrentUser()).thenReturn(user);
    }

    @Test
    void addItem_calculatesTotals() {
        Product product = TestData.product(10L, 5);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(inv -> inv.getArgument(0));

        CartResponse response = cartService.addItem(new CartItemRequest(10L, 2));

        assertThat(response.totalItems()).isEqualTo(2);
        assertThat(response.subtotal()).isEqualByComparingTo("2000");
        assertThat(response.discount()).isEqualByComparingTo("200");
        assertThat(response.tax()).isEqualByComparingTo("180");
        assertThat(response.shipping()).isEqualByComparingTo("50");
        assertThat(response.grandTotal()).isEqualByComparingTo("2030");
    }

    @Test
    void addItem_moreThanStock_throws() {
        Product product = TestData.product(10L, 2);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));

        assertThrows(InsufficientStockException.class,
                () -> cartService.addItem(new CartItemRequest(10L, 3)));
        verify(cartItemRepository, never()).save(any());
    }

    @Test
    void addItem_sameProductTwice_increasesQuantity() {
        Product product = TestData.product(10L, 5);
        TestData.cartItem(cart, product, 1);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));

        CartResponse response = cartService.addItem(new CartItemRequest(10L, 2));

        assertThat(response.items()).hasSize(1);
        assertThat(response.items().get(0).quantity()).isEqualTo(3);
    }

    @Test
    void addItem_inactiveProduct_throwsNotFound() {
        Product product = TestData.product(10L, 5);
        product.setActive(false);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));

        assertThrows(ResourceNotFoundException.class,
                () -> cartService.addItem(new CartItemRequest(10L, 1)));
    }

    @Test
    void removeItem_removesFromCart() {
        CartItem item = TestData.cartItem(cart, TestData.product(10L, 5), 1);
        when(cartItemRepository.findByIdAndCartUserId(100L, 1L)).thenReturn(Optional.of(item));

        CartResponse response = cartService.removeItem(100L);

        assertThat(response.items()).isEmpty();
        assertThat(response.grandTotal()).isEqualByComparingTo("0");
    }
}
