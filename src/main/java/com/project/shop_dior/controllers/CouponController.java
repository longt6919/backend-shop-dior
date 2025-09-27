package com.project.shop_dior.controllers;

import com.project.shop_dior.dtos.CouponConditionDTO;
import com.project.shop_dior.dtos.CouponDTO;
import com.project.shop_dior.exception.DataNotFoundException;
import com.project.shop_dior.models.Color;
import com.project.shop_dior.models.Coupon;
import com.project.shop_dior.models.CouponCondition;
import com.project.shop_dior.responses.CouponCalculationResponse;
import com.project.shop_dior.responses.ResponseObject;
import com.project.shop_dior.service.CouponService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/coupons")
@RequiredArgsConstructor
public class CouponController {
    private final CouponService couponService;
    @GetMapping("/calculate")
    public ResponseEntity<ResponseObject> calculateCouponValue(
            @RequestParam("couponCode") String couponCode,
            @RequestParam("totalAmount") double totalAmount){
        double finalAmount = couponService.calculateCouponValue(couponCode,totalAmount);
        CouponCalculationResponse couponCalculationResponse = CouponCalculationResponse.builder()
                .result(finalAmount)
                .build();
        return ResponseEntity.ok(new ResponseObject(
                "Calculate coupon successfully",
                HttpStatus.OK,
                couponCalculationResponse
        ));
    }
    @DeleteMapping("/{couponId}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable Long couponId) throws DataNotFoundException {
        couponService.deleteCoupon(couponId);
        return ResponseEntity.noContent().build(); // 204
    }
    @PostMapping("")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<?> create(@Valid @RequestBody CouponDTO couponDTO) {
        Coupon saved = couponService.createCouponWithDefaultCondition(couponDTO);
        return ResponseEntity.ok(saved);
    }
    @GetMapping("")
    public ResponseEntity<Page<Coupon>> getAllCoupons(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int limit
    ) {
        Pageable pageable = PageRequest.of(page, limit);
        Page<Coupon> coupons = couponService.listAll(pageable);
        return ResponseEntity.ok(coupons);
    }
    @PutMapping("/block/{couponId}/{active}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<String> blockOrEnable(
            @Valid @PathVariable long couponId,
            @Valid @PathVariable int active
    ) {
        try {
            couponService.blockOrEnable(couponId, active > 0);
            String message = active > 0 ? "Successfully enabled couponId." : "Successfully blocked the couponId.";
            return ResponseEntity.ok().body(message);
        } catch (DataNotFoundException e) {
            return ResponseEntity.badRequest().body("coupon not found.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/{couponId}/conditions")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<List<CouponCondition>> listConditions(@PathVariable Long couponId) {
        List<CouponCondition> items = couponService.findCouponConditionByCoupon(couponId);
        return ResponseEntity.ok(items);
    }
    @PostMapping("/conditions/{couponId}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<?> addCondition(@PathVariable Long couponId,
                                          @Valid @RequestBody CouponConditionDTO couponConditionDTO) {
        try {
            CouponCondition couponCondition = couponService.addCouponCondition(couponId, couponConditionDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(couponCondition);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
    @PutMapping("/{couponId}/conditions/{conditionId}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<?> updateConditionOfCoupon(@PathVariable Long couponId,
                                                     @PathVariable Long conditionId,
                                                     @Valid @RequestBody CouponConditionDTO dto) {
        try {
            CouponCondition updated = couponService.updateCouponConditionOfCoupon(couponId, conditionId, dto);
            return ResponseEntity.ok(updated);
        } catch (DataNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
