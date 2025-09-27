package com.project.shop_dior.service;

import com.project.shop_dior.dtos.CouponConditionDTO;
import com.project.shop_dior.dtos.CouponDTO;
import com.project.shop_dior.dtos.OrderDTO;
import com.project.shop_dior.exception.DataNotFoundException;
import com.project.shop_dior.models.Brand;
import com.project.shop_dior.models.Coupon;
import com.project.shop_dior.models.CouponCondition;
import com.project.shop_dior.models.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CouponService {
    double calculateCouponValue(String couponCode, double totalAmount);
    void blockOrEnable(Long couponId, Boolean active) throws DataNotFoundException;
    Page<Coupon> listAll(Pageable pageable);
    Coupon createCoupon(CouponDTO couponDTO) throws DataNotFoundException;
    Coupon createCouponWithDefaultCondition(CouponDTO couponDTO);
    List<CouponCondition> findCouponConditionByCoupon(Long couponId); // đúng yêu cầu
    CouponCondition addCouponCondition(Long couponId, CouponConditionDTO dto);
    void deleteCoupon(Long couponId) throws DataNotFoundException;
    CouponCondition updateCouponConditionOfCoupon(Long couponId, Long conditionId, CouponConditionDTO dto) throws DataNotFoundException;

}
