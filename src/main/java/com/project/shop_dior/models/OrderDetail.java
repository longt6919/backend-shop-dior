package com.project.shop_dior.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "order_details")
@Builder
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonBackReference
    private Order order;
    @ManyToOne
    @JoinColumn(name = "product_detail_id")
    private ProductDetail productDetail;
    @Column(name = "price",nullable = false)
    private BigDecimal price;
    @Column(name = "number_of_products",nullable = false)
    private int numberOfProducts;
    @Column(name = "total_money",nullable = false)
    private BigDecimal totalMoney;
    @ManyToOne
    @JoinColumn(name = "coupon_id")
    @JsonManagedReference
    private Coupon coupon;
    public BigDecimal getTotalOrigin(Order order) {
        return order.getOrderDetails().stream()
                .map(detail -> detail.getPrice().multiply(BigDecimal.valueOf(detail.getNumberOfProducts())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
