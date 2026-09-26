package com.nexora.ecommerce.service.impl;

import com.nexora.ecommerce.dto.ProductResponse;
import com.nexora.ecommerce.entity.Product;
import com.nexora.ecommerce.entity.User;
import com.nexora.ecommerce.entity.Wishlist;
import com.nexora.ecommerce.entity.WishlistItem;
import com.nexora.ecommerce.exception.ResourceNotFoundException;
import com.nexora.ecommerce.mapper.ProductMapper;
import com.nexora.ecommerce.repository.ProductRepository;
import com.nexora.ecommerce.repository.WishlistRepository;
import com.nexora.ecommerce.service.WishlistService;
import com.nexora.ecommerce.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional
    public List<ProductResponse> getWishlist() {
        return toResponse(getOrCreate(securityUtils.getCurrentUser()));
    }

    @Override
    @Transactional
    public List<ProductResponse> addProduct(Long productId) {
        Wishlist wishlist = getOrCreate(securityUtils.getCurrentUser());
        Product product = productRepository.findById(productId)
                .filter(Product::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));

        boolean alreadyThere = wishlist.getItems().stream()
                .anyMatch(i -> i.getProduct().getId().equals(productId));

        if (!alreadyThere) { // adding twice is harmless
            WishlistItem item = new WishlistItem();
            item.setWishlist(wishlist);
            item.setProduct(product);
            wishlist.getItems().add(item);
        }
        return toResponse(wishlist);
    }

    @Override
    @Transactional
    public List<ProductResponse> removeProduct(Long productId) {
        Wishlist wishlist = getOrCreate(securityUtils.getCurrentUser());
        wishlist.getItems().removeIf(i -> i.getProduct().getId().equals(productId));
        return toResponse(wishlist);
    }

    private Wishlist getOrCreate(User user) {
        return wishlistRepository.findByUserId(user.getId()).orElseGet(() -> {
            Wishlist w = new Wishlist();
            w.setUser(user);
            return wishlistRepository.save(w);
        });
    }

    private List<ProductResponse> toResponse(Wishlist wishlist) {
        return wishlist.getItems().stream()
                .map(WishlistItem::getProduct)
                .filter(Product::isActive)
                .map(ProductMapper::toResponse)
                .toList();
    }
}
