package com.project.shop_dior.repository;

import com.project.shop_dior.models.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon,Long> {
    Optional<Coupon> findByCode(String couponCode);
    Optional<Coupon> findByCodeAndActiveTrue(String code);
    boolean existsByCode(String code);

}

