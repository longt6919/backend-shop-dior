package com.project.shop_dior.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryRevenueResponse {
    private int year;
    private int month;
    private BigDecimal total;
}
