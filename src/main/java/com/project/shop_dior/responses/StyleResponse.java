package com.project.shop_dior.responses;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shop_dior.models.Category;
import com.project.shop_dior.models.Style;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StyleResponse {
    @JsonProperty("message")
    private String message;
    @JsonProperty("errors")
    private List<String> errors;
    @JsonProperty("style")
    private Style style;
}
