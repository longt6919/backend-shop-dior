package com.project.shop_dior.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shop_dior.models.Category;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryResponse {
@JsonProperty("message")
private String message;
    @JsonProperty("errors")
    private List<String> errors;
    @JsonProperty("category")
    private Category category;
}
