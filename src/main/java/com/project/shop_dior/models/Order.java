package com.project.shop_dior.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "orders")
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JoinColumn(name = "id")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Column(name = "fullname",length = 100)
    private String fullName;
    @Column(name = "email", length = 100)
    private String email;
    @Column(name = "phone_number", nullable = false,length = 20)
    private String phoneNumber;
    @Column(name = "address",length = 200)
    private String address;
    @Column(name = "note", length = 100)
    private String note;
    @Column(name = "order_date")
    private Date orderDate;
    @Column(name = "status",length = 20)
    private String status;
    @Column(name = "total_money")
    private BigDecimal totalMoney;
    @Column(name = "shipping_method")
    private String shippingMethod;
    @Column(name = "shipping_address")
    private String shippingAddress;
    @Column(name = "shipping_dated")
    private LocalDate shippingDate;
    @Column(name = "delivery_date")
    private Date deliveryDate;
    @Column(name = "payment_method")
    private String paymentMethod;
    @Column(name = "active")
    private Boolean active;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<OrderDetail> orderDetails = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "coupon_id")
    @JsonManagedReference
    private Coupon coupon;
}
