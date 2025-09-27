package com.project.shop_dior.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.math.BigDecimal;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailDTO {
    @JsonProperty("order_id")
    @Min(value = 1,message = "Order's ID must be >0")
    private Long orderId;
    @Min(value = 1,message = "Product's ID must be >=0")
    @JsonProperty("product_detail_id")
    private Long productDetailId;
    @Min(value = 0,message = "Price must be >=0")
    @JsonProperty("price")
    private BigDecimal price;
    @Min(value = 1,message = "number of product must be >0")
    @JsonProperty("number_of_products")
    private int numberOfProducts;
    @JsonProperty("color")
    private String color;
    @JsonProperty("size")
    private String size;
    @Min(value = 1,message = "total money must be >0")
    @JsonProperty("total_money")
    private BigDecimal totalMoney;
}
