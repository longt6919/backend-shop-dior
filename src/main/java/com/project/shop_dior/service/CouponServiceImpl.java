package com.project.shop_dior.service;

import com.project.shop_dior.dtos.CouponConditionDTO;
import com.project.shop_dior.dtos.CouponDTO;
import com.project.shop_dior.exception.DataNotFoundException;
import com.project.shop_dior.models.Brand;
import com.project.shop_dior.models.Coupon;
import com.project.shop_dior.models.CouponCondition;
import com.project.shop_dior.repository.CouponConditionRepository;
import com.project.shop_dior.repository.CouponRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class CouponServiceImpl implements CouponService{
    private final CouponRepository couponRepository;
    private final CouponConditionRepository couponConditionRepository;
    @Override
    public double calculateCouponValue(String couponCode, double totalAmount) {
        Coupon coupon = couponRepository.findByCode(couponCode)
                .orElseThrow(()->new IllegalArgumentException("Coupon sai"));
        if (!coupon.isActive()){
            throw new IllegalArgumentException("Coupon đã hết hạn sử dụng");
        }
        double discount = calculateDiscount(coupon,totalAmount);
        double finalAmount = totalAmount - discount;
        return finalAmount;
    }
    @Transactional
    @Override
    public CouponCondition updateCouponConditionOfCoupon(Long couponId, Long conditionId, CouponConditionDTO dto) throws DataNotFoundException {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new EntityNotFoundException("Coupon not found: " + couponId));

        CouponCondition cc = couponConditionRepository.findById(conditionId)
                .orElseThrow(() -> new EntityNotFoundException("Condition not found: " + conditionId));

        // Đảm bảo condition thuộc coupon này
        if (!cc.getCoupon().getId().equals(coupon.getId())) {
            throw new IllegalArgumentException("Condition does not belong to coupon " + couponId);
        }
        if (dto.getValue() == null || dto.getValue().isBlank()) {
            throw new IllegalArgumentException("value is required");
        }

        // Validate discountAmount
        if (dto.getDiscountAmount() == null) {
            throw new DataNotFoundException("Số tiền giảm không được để trống");
        }
        BigDecimal discount = dto.getDiscountAmount();
        if (discount.compareTo(BigDecimal.ZERO) < 0) {
            throw new DataNotFoundException("Giảm giá không được âm");
        }
        if (discount.compareTo(BigDecimal.valueOf(20)) > 0) {
            throw new DataNotFoundException("Giảm giá tối đa 20%");
        }


        cc.setAttribute(dto.getAttribute());
        cc.setOperator(dto.getOperator());
        cc.setValue(dto.getValue());
        cc.setDiscountAmount(dto.getDiscountAmount());

        return couponConditionRepository.save(cc);
    }


    @Override
    public void blockOrEnable(Long couponId, Boolean active) throws DataNotFoundException {
        Coupon existingCoupon = couponRepository.findById(couponId)
                .orElseThrow(()->new DataNotFoundException("Coupon not found"));
        existingCoupon.setActive(active);
        couponRepository.save(existingCoupon);
    }


    @Override
    public Page<Coupon> listAll(Pageable pageable) {
        return couponRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public Coupon createCoupon(CouponDTO couponDTO) throws DataNotFoundException {
        String code = couponDTO.getCode().trim();
        if (couponRepository.existsByCode(code)) {
            throw new IllegalArgumentException("Code đã tồn tại");
        }
        Coupon coupon = Coupon.builder()
                .code(code)
                .active(true)
                .build();
        return couponRepository.save(coupon);
    }

    @Override
    @Transactional
    public Coupon createCouponWithDefaultCondition(CouponDTO couponDTO) {
        String code = couponDTO.getCode().trim();
        if (couponRepository.existsByCode(code)) {
            throw new IllegalArgumentException("Code đã tồn tại");
        }
        Coupon coupon = new Coupon();
        coupon.setCode(couponDTO.getCode());
        coupon.setActive(couponDTO.isActive());
        coupon = couponRepository.save(coupon);
        // 2) Tạo condition mặc định
        CouponCondition condition = new CouponCondition();
        condition.setCoupon(coupon);
        condition.setAttribute("minimum_amount");         // attribute mặc định
        condition.setOperator(">=");         // operator mặc định
        condition.setValue("0");             // value mặc định
        condition.setDiscountAmount(BigDecimal.valueOf(0.00)); // discount mặc định
        // 3) Lưu condition
        couponConditionRepository.save(condition);
        // Optional: nếu entity Coupon có quan hệ OneToMany, có thể add vào list để trả về đầy đủ
        if (coupon.getConditions() != null) {
            coupon.getConditions().add(condition);
        }
        return coupon;
    }


    @Transactional
    @Override
    public void deleteCoupon(Long couponId) throws DataNotFoundException {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new DataNotFoundException("Not found coupon id=" + couponId));
        // Xóa parent -> JPA tự xóa children nhờ cascade+orphanRemoval
        couponRepository.delete(coupon);
    }

    @Override
    public List<CouponCondition> findCouponConditionByCoupon(Long couponId) {
        return couponConditionRepository.findByCouponId(couponId);
    }

    @Override
    public CouponCondition addCouponCondition(Long couponId, CouponConditionDTO dto) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("Coupon không tồn tại"));

        CouponCondition cond = CouponCondition.builder()
                .coupon(coupon)
                .attribute(dto.getAttribute())
                .operator(dto.getOperator())
                .value(dto.getValue())
                .discountAmount(dto.getDiscountAmount())
                .build();
        return couponConditionRepository.save(cond);    }

    private double calculateDiscount(Coupon coupon, double totalAmount) {
        //→ Truy vấn tất cả các CouponCondition thuộc coupon hiện tại.
        List<CouponCondition> conditions = couponConditionRepository
                .findByCouponId(coupon.getId());
        double discount = 0.0;
        double updatedTotalAmount = totalAmount;
        // Duyệt từng điều kiện để áp dụng giảm giá
        // Mỗi điều kiện là một dòng trong bảng coupon_conditions (EAV).
        for (CouponCondition condition : conditions) {
            //EAV(Entity - Attribute - Value)  EAV giúp bạn cấu hình điều kiện bằng dữ liệu chứ không cần viết cứng trong code.
            String attribute = condition.getAttribute();
            String operator = condition.getOperator();
            String value = condition.getValue();
            //Tính phần trăm giảm giá
            double percentDiscount = Double.valueOf(
                    String.valueOf(condition.getDiscountAmount()));
            //Xử lý điều kiện "minimum_amount"
            if (attribute.equals("minimum_amount")) {
                if (operator.equals(">") && updatedTotalAmount > Double.parseDouble(value)) {
                    discount += updatedTotalAmount * percentDiscount / 100;
                }
                if (operator.equals(">=") && updatedTotalAmount > Double.parseDouble(value)) {
                    discount += updatedTotalAmount * percentDiscount / 100;
                }
                //Xử lý điều kiện "applicable_date"
            } else if (attribute.equals("applicable_date")) {
                LocalDate applicableDate = LocalDate.parse(value);
                LocalDate currentDate = LocalDate.now();
                if (operator.equalsIgnoreCase("BETWEEN")
                        && currentDate.isEqual(applicableDate)) {
                    discount += updatedTotalAmount * percentDiscount / 100;
                }
            }
            //Cập nhật lại updatedTotalAmount sau mỗi điều kiện
            updatedTotalAmount = updatedTotalAmount - discount;
        }
        return discount;
    }
}
