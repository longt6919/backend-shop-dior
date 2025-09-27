package com.project.shop_dior.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemRequestDTO {
    @JsonProperty("detail_id")
    private Long detailId;
    @JsonProperty("qty")
    private Integer qty;
}
