package com.project.shop_dior.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shop_dior.models.ProductDetail;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {
//    @NotBlank
//    @Size(min = 3,max = 200, message = "3 and 200 character")
    private String name;
//    @NotNull(message = "Not null")
//    @Min(value = 0)
//    @Max(value = 10000000)
    private BigDecimal price;
    private String thumbnail;
    private String description;
    @JsonProperty("category_id")
    private Long categoryId;
    @JsonProperty("origin_id")
    private Long originId;
    @JsonProperty("style_id")
    private Long styleId;
    @JsonProperty("material_id")
    private Long materialId;
    @JsonProperty("brand_id")
    private Long brandId;
    private List<MultipartFile> file;
    private List<ProductDetailDTO> productDetails;
}
