package com.project.shop_dior.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
public class CartItemDTO {
//    @JsonProperty("product_detail_id")
//    private Long productDetailId;
//    @JsonProperty("quantity")
//    private Integer quantity;
@JsonProperty("product_id")
private Long productId;
    @JsonProperty("size_id")
    private Long sizeId;
    @JsonProperty("color_id")
    private Long colorId;
    @JsonProperty("quantity")
    private Integer quantity;
}
