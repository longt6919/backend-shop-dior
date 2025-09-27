package com.project.shop_dior.responses;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProductListResponse {
    private List<ProductResponse> products;
    private int totalPages;
}
