package com.project.shop_dior.responses;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shop_dior.dtos.ProductDetailDTO;
import com.project.shop_dior.models.ProductDetail;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ProductDetailResponse {
    @JsonProperty("product_detail_id")
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


    public ProductDetailResponse(ProductDetail detail) {
        this.sizeId = detail.getSize().getId();
        this.colorId = detail.getColor().getId();
        this.quantity = detail.getQuantity();
    }

    public static ProductDetailResponse fromEntity(ProductDetail productDetail) {
        return ProductDetailResponse.builder()
                .id(productDetail.getId())
                .sizeId(productDetail.getSize().getId())
                .sizeName(productDetail.getSize().getName())
                .colorId(productDetail.getColor().getId())
                .colorName(productDetail.getColor().getName())
                .quantity(productDetail.getQuantity())
                .build();
    }
}
