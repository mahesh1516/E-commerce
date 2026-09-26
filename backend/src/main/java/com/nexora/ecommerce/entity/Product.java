package com.nexora.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products", indexes = {
        @Index(name = "idx_product_name", columnList = "name"),
        @Index(name = "idx_product_brand", columnList = "brand"),
        @Index(name = "idx_product_price", columnList = "price")
})
@Getter
@Setter
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    /** One "Key: Value" pair per line, shown as a specs table in the UI. */
    @Column(columnDefinition = "TEXT")
    private String specifications;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    /** Optional sale price; must be lower than price. */
    @Column(precision = 12, scale = 2)
    private BigDecimal discountPrice;

    @Column(length = 80)
    private String brand;

    @Column(nullable = false, unique = true, length = 60)
    private String sku;

    /** Main image; extra images live in product_images. */
    @Column(length = 500)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    /** Average review rating (0 when no reviews). */
    @Column(nullable = false)
    private double rating = 0.0;

    @Column(nullable = false)
    private int reviewCount = 0;

    /** false = soft deleted / hidden from the store. */
    @Column(nullable = false)
    private boolean active = true;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Inventory inventory;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    private List<ProductImage> images = new ArrayList<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /** Price the customer actually pays per unit. */
    public BigDecimal getEffectivePrice() {
        if (discountPrice != null && discountPrice.compareTo(price) < 0) {
            return discountPrice;
        }
        return price;
    }

    public int getStock() {
        return inventory == null ? 0 : inventory.getQuantity();
    }
}
