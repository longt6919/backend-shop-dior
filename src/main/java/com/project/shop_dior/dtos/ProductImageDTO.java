package com.project.shop_dior.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageDTO {
    @JsonProperty( "product_id")
    @Size(min=1,message = "Product's ID must be > 0")
    private Long productId;
    @Size(min = 5,max = 200,message = "Image's name")
    @JsonProperty("image_url")
    private String imageUrl;
}
