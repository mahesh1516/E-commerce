package com.nexora.ecommerce.service.impl;

import com.nexora.ecommerce.dto.CartItemRequest;
import com.nexora.ecommerce.dto.CartResponse;
import com.nexora.ecommerce.entity.Cart;
import com.nexora.ecommerce.entity.CartItem;
import com.nexora.ecommerce.entity.Product;
import com.nexora.ecommerce.entity.User;
import com.nexora.ecommerce.exception.InsufficientStockException;
import com.nexora.ecommerce.exception.InvalidRequestException;
import com.nexora.ecommerce.exception.ResourceNotFoundException;
import com.nexora.ecommerce.mapper.CartMapper;
import com.nexora.ecommerce.repository.CartItemRepository;
import com.nexora.ecommerce.repository.CartRepository;
import com.nexora.ecommerce.repository.ProductRepository;
import com.nexora.ecommerce.service.CartService;
import com.nexora.ecommerce.util.PriceCalculator;
import com.nexora.ecommerce.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    static final int MAX_QTY_PER_ITEM = 10;

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final PriceCalculator priceCalculator;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional
    public CartResponse getCart() {
        return toResponse(getOrCreateCart(securityUtils.getCurrentUser()));
    }

    @Override
    @Transactional
    public CartResponse addItem(CartItemRequest request) {
        Cart cart = getOrCreateCart(securityUtils.getCurrentUser());

        Product product = productRepository.findById(request.productId())
                .filter(Product::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Product", request.productId()));

        Optional<CartItem> existing = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(product.getId()))
                .findFirst();

        int newQuantity = existing.map(CartItem::getQuantity).orElse(0) + request.quantity();
        checkQuantity(product, newQuantity);

        if (existing.isPresent()) {
            existing.get().setQuantity(newQuantity);
        } else {
            CartItem item = new CartItem();
            item.setCart(cart);
            item.setProduct(product);
            item.setQuantity(newQuantity);
            cartItemRepository.save(item); // assigns the id
            cart.getItems().add(item);
        }
        return toResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse updateItem(Long itemId, int quantity) {
        CartItem item = findOwnItem(itemId);
        checkQuantity(item.getProduct(), quantity);
        item.setQuantity(quantity);
        return toResponse(item.getCart());
    }

    @Override
    @Transactional
    public CartResponse removeItem(Long itemId) {
        CartItem item = findOwnItem(itemId);
        Cart cart = item.getCart();
        cart.getItems().remove(item); // orphanRemoval deletes the row
        return toResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse clearCart() {
        Cart cart = getOrCreateCart(securityUtils.getCurrentUser());
        cart.getItems().clear();
        return toResponse(cart);
    }

    // ------------------------------------------------------------

    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUserId(user.getId()).orElseGet(() -> {
            Cart cart = new Cart();
            cart.setUser(user);
            return cartRepository.save(cart);
        });
    }

    private CartItem findOwnItem(Long itemId) {
        Long userId = securityUtils.getCurrentUser().getId();
        // Only finds the item if it belongs to THIS user's cart
        return cartItemRepository.findByIdAndCartUserId(itemId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item", itemId));
    }

    private void checkQuantity(Product product, int quantity) {
        if (quantity > MAX_QTY_PER_ITEM) {
            throw new InvalidRequestException("Maximum " + MAX_QTY_PER_ITEM + " units per product");
        }
        int stock = product.getStock();
        if (stock <= 0) {
            throw new InsufficientStockException(product.getName() + " is out of stock");
        }
        if (quantity > stock) {
            throw new InsufficientStockException("Only " + stock + " unit(s) of "
                    + product.getName() + " available");
        }
    }

    private CartResponse toResponse(Cart cart) {
        List<PriceCalculator.Line> lines = cart.getItems().stream()
                .map(i -> new PriceCalculator.Line(
                        i.getProduct().getPrice(), i.getProduct().getEffectivePrice(), i.getQuantity()))
                .toList();
        return CartMapper.toResponse(cart, priceCalculator.calculate(lines));
    }
}
