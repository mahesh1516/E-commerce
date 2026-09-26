package com.nexora.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // Snapshot of product data at purchase time
    @Column(nullable = false, length = 200)
    private String productName;

    @Column(length = 500)
    private String imageUrl;

    /** Original (list) price per unit. */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    /** Price actually paid per unit (after discount). */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal finalUnitPrice;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal lineTotal;
}
