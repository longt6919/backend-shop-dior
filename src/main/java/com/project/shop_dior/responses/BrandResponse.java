package com.project.shop_dior.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shop_dior.models.Brand;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BrandResponse {
    @JsonProperty("message")
    private String message;
    @JsonProperty("errors")
    private List<String> errors;
    @JsonProperty("brand")
    private Brand brand;
}
