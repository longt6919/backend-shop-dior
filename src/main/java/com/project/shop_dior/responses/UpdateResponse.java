package com.project.shop_dior.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateResponse {
    @JsonProperty("message")
    private String message;
    @JsonProperty("token")
    private String token;
}
