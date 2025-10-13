package com.project.shop_dior.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shop_dior.dtos.ProductDetailDTO;
import com.project.shop_dior.models.Product;
import com.project.shop_dior.models.ProductDetail;
import com.project.shop_dior.models.ProductImage;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public class ProductResponse extends ResponseBaseEntity{
    private Long id;
    private String name;
    private BigDecimal price;
    private String thumbnail;
    private String description;
    @JsonProperty("is_active")
    private Boolean active;
    @JsonProperty("category_id")
    private Long categoryId;
    @JsonProperty("category_name")
    private String categoryName;
    @JsonProperty("origin_id")
    private Long originId;
    @JsonProperty("origin_name")
    private String originName;
    @JsonProperty("style_id")
    private Long styleId;
    @JsonProperty("style_name")
    private String styleName;
    @JsonProperty("material_id")
    private Long materialId;
    @JsonProperty("material_name")
    private String materialName;
    @JsonProperty("brand_id")
    private Long brandId;
    @JsonProperty("brand_name")
    private String brandName;
    @JsonProperty("product_images")
    private List<ProductImage> productImages = new ArrayList<>();
    @JsonProperty("product_details")
    private List<ProductDetailDTO> productDetails;
    private int totalPages;
    public static ProductResponse fromProduct(Product product,List<ProductDetailDTO> detils){
        ProductResponse productResponse = ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .thumbnail(product.getThumbnail())
                .description(product.getDescription())
                .active(product.isActive())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .originId(product.getOrigin().getId())
                .originName(product.getOrigin().getName())
                .styleId(product.getStyle().getId())
                .styleName(product.getStyle().getName())
                .materialId(product.getMaterial().getId())
                .materialName(product.getMaterial().getName())
                .brandId(product.getBrand().getId())
                .brandName(product.getBrand().getName())
                .productImages(product.getProductImages())

                .productDetails(detils)
                .build();
        productResponse.setCreateAt(product.getCreateAt());
        productResponse.setUpdateAt(product.getUpdateAt());
        return productResponse;
    }
}
