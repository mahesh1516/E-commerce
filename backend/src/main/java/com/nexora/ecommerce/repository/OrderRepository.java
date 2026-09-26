package com.nexora.ecommerce.repository;

import com.nexora.ecommerce.entity.Order;
import com.nexora.ecommerce.entity.OrderStatus;
import com.nexora.ecommerce.entity.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByUserId(Long userId, Pageable pageable);

    Optional<Order> findByIdAndUserId(Long id, Long userId);

    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    List<Order> findTop5ByOrderByCreatedAtDesc();

    /** Returns null when there are no matching orders. */
    @Query("select sum(o.grandTotal) from CustomerOrder o "
            + "where o.status <> :excluded and o.payment.status = :paid")
    BigDecimal sumRevenue(@Param("excluded") OrderStatus excluded,
                          @Param("paid") PaymentStatus paid);

    /** Each row: [OrderStatus, Long count] */
    @Query("select o.status, count(o) from CustomerOrder o group by o.status")
    List<Object[]> countByStatus();
}
