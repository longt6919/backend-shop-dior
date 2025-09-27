package com.project.shop_dior.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponDTO {
    private Long id;
    @NotBlank
    @Size(max = 50)
    private String code;
    @JsonProperty("coupon_conditions")
    private List<CouponConditionDTO> couponConditions = new ArrayList<>();
    private boolean active;
}
