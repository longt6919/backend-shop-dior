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
public class CartItemCheckDTO {
    @JsonProperty("detail_id")
    private Long detailId;
    private int requested;  // số lượng yêu cầu
    private int available;  // tồn hiện có
    private boolean ok;
}
