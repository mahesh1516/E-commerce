package com.nexora.ecommerce.service.impl;

import com.nexora.ecommerce.dto.ReviewRequest;
import com.nexora.ecommerce.dto.ReviewResponse;
import com.nexora.ecommerce.entity.Product;
import com.nexora.ecommerce.entity.Review;
import com.nexora.ecommerce.entity.User;
import com.nexora.ecommerce.exception.DuplicateResourceException;
import com.nexora.ecommerce.exception.ResourceNotFoundException;
import com.nexora.ecommerce.mapper.ReviewMapper;
import com.nexora.ecommerce.repository.ProductRepository;
import com.nexora.ecommerce.repository.ReviewRepository;
import com.nexora.ecommerce.service.ReviewService;
import com.nexora.ecommerce.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional
    public ReviewResponse addReview(Long productId, ReviewRequest request) {
        User user = securityUtils.getCurrentUser();
        Product product = findActiveProduct(productId);

        if (reviewRepository.existsByUserIdAndProductId(user.getId(), productId)) {
            throw new DuplicateResourceException("You have already reviewed this product");
        }

        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setRating(request.rating());
        review.setComment(request.comment() == null ? null : request.comment().trim());
        reviewRepository.save(review);

        // Keep the product's average rating up to date
        Double average = reviewRepository.averageRating(productId);
        product.setRating(average == null ? 0.0 : average);
        product.setReviewCount((int) reviewRepository.countByProductId(productId));

        return ReviewMapper.toResponse(review);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviews(Long productId) {
        findActiveProduct(productId);
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId).stream()
                .map(ReviewMapper::toResponse)
                .toList();
    }

    private Product findActiveProduct(Long id) {
        return productRepository.findById(id)
                .filter(Product::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
    }
}
