package com.project.shop_dior.repository;

import com.project.shop_dior.models.CouponCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CouponConditionRepository  extends JpaRepository<CouponCondition,Long> {
    List<CouponCondition> findByCouponId(Long couponId);

}
