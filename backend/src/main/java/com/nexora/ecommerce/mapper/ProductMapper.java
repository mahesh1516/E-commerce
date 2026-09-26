package com.nexora.ecommerce.mapper;

import com.nexora.ecommerce.dto.ProductResponse;
import com.nexora.ecommerce.entity.Category;
import com.nexora.ecommerce.entity.Product;
import com.nexora.ecommerce.entity.ProductImage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public final class ProductMapper {

    private ProductMapper() {
    }

    public static ProductResponse toResponse(Product p) {
        Category c = p.getCategory();
        List<String> images = p.getImages().stream().map(ProductImage::getImageUrl).toList();
        int stock = p.getStock();

        return new ProductResponse(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getSpecifications(),
                p.getPrice(),
                p.getDiscountPrice(),
                p.getEffectivePrice(),
                discountPercent(p),
                p.getBrand(),
                p.getSku(),
                p.getImageUrl(),
                images,
                c.getId(),
                c.getName(),
                c.getSlug(),
                stock,
                stock > 0,
                Math.round(p.getRating() * 10.0) / 10.0,
                p.getReviewCount(),
                p.isActive(),
                p.getCreatedAt(),
                p.getUpdatedAt());
    }

    /** (price - discountPrice) / price * 100, rounded. 0 if no discount. */
    static int discountPercent(Product p) {
        BigDecimal price = p.getPrice();
        BigDecimal effective = p.getEffectivePrice();
        if (price == null || price.signum() == 0 || effective.compareTo(price) >= 0) {
            return 0;
        }
        return price.subtract(effective)
                .multiply(BigDecimal.valueOf(100))
                .divide(price, 0, RoundingMode.HALF_UP)
                .intValue();
    }
}
