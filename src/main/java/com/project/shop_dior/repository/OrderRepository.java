package com.project.shop_dior.repository;

import com.project.shop_dior.models.Order;
import com.project.shop_dior.responses.OrderListResponse;
import com.project.shop_dior.responses.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Long> {
    @EntityGraph(attributePaths = "orderDetails")
    List<Order> findByUserId(Long userId);

@EntityGraph(attributePaths = "orderDetails")
@Query("SELECT o FROM Order o WHERE " +
        "(:keyword IS NULL OR :keyword = '' OR " +
        "o.phoneNumber LIKE %:keyword% " +
        "OR o.address LIKE %:keyword% " +
        "OR o.status LIKE %:keyword% " +
        "OR o.paymentMethod LIKE %:keyword% " +
        "OR o.email LIKE %:keyword%)")
    Page<Order> findByKeyword(String keyword, Pageable pageable);


@Query("""
        SELECT COALESCE(SUM(o.totalMoney), 0)
          FROM Order o
         WHERE o.status = 'delivered'
           AND o.deliveryDate >= :start
           AND o.deliveryDate < :end
    """)
    BigDecimal sumDeliveredBetween(LocalDateTime start, LocalDateTime end);

    // CHỈ đơn đã giao (delivered)
    @EntityGraph(attributePaths = "orderDetails")
    @Query("""
SELECT o FROM Order o WHERE 
o.status = 'delivered' AND
(:keyword IS NULL OR :keyword = '' OR 
 o.phoneNumber  LIKE %:keyword% OR 
 o.paymentMethod LIKE %:keyword% OR 
 o.email        LIKE %:keyword%)
""")
    Page<Order> findDeliveredOrders(String keyword,Pageable pageable);

    @EntityGraph(attributePaths = "orderDetails")
    @Query("""
SELECT o FROM Order o
WHERE o.status = 'delivered'
  AND LOWER(o.shippingMethod) = 'counter'
  AND (
    :keyword IS NULL OR :keyword = '' OR
    o.phoneNumber   LIKE %:keyword% OR
    o.paymentMethod LIKE %:keyword% OR
    o.email         LIKE %:keyword%
  )
""")
    Page<Order> findDeliveredOrdersAtCounter(String keyword, Pageable pageable);

    @EntityGraph(attributePaths = "orderDetails")
    @Query("""
SELECT o FROM Order o
WHERE o.status = 'delivered'
  AND (o.shippingMethod IS NULL OR LOWER(o.shippingMethod) <> 'counter')
  AND (
    :keyword IS NULL OR :keyword = '' OR
    o.phoneNumber   LIKE %:keyword% OR
    o.paymentMethod LIKE %:keyword% OR
    o.email         LIKE %:keyword%
  )
""")
    Page<Order> findDeliveredOrdersNotCounter(String keyword, Pageable pageable);

}
