package com.project.shop_dior.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class ColorListResponse {
    private List<ColorResponse> colors;
    private int totalPages;

}
