package com.project.shop_dior.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shop_dior.models.ProductDetail;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDetailDTO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("size_id")
    private Long sizeId;

    @JsonProperty("color_id")
    private Long colorId;

    @JsonProperty("color_name")
    private String colorName;

    @JsonProperty("size_name")
    private String sizeName;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 0, message = "Số lượng phải >= 0")
    private Integer quantity;

    public static ProductDetailDTO fromEntity(ProductDetail productDetail) {
        return ProductDetailDTO.builder()
                .id(productDetail.getId())
                .sizeId(productDetail.getSize().getId())
                .sizeName(productDetail.getSize().getName())
                .colorId(productDetail.getColor().getId())
                .colorName(productDetail.getColor().getName())
                .quantity(productDetail.getQuantity())
                .build();
    }
}
