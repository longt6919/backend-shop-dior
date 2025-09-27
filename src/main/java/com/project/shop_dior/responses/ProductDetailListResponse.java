package com.project.shop_dior.responses;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProductDetailListResponse {
    private List<ProductDetailResponse> productDetails;
    private int totalPages;
}
