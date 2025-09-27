package com.project.shop_dior.dtos;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponConditionDTO {
    @NotBlank
    private String attribute;
    @NotBlank(message = "operator is required")
    @Pattern(
            regexp = ">=|<=|>|<|==|equals",
            message = "operator must be one of: >=, <=, >, <, =="
    )    @NotBlank private String operator;
    @NotNull(message = "value is required")
    @DecimalMin(value = "0.00", inclusive = true, message = "value must be >= 0")
    @NotBlank private String value;
    @NotNull
    @NotNull(message = "discountAmount is required")
    @DecimalMin(value = "0.00", inclusive = true, message = "discountAmount must be >= 0")
    private BigDecimal discountAmount;
}
