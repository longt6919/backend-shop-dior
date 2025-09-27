package com.project.shop_dior.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {
    @JsonProperty("message")
    private String message;
    @JsonProperty("token")
    private String token;
    private String tokenType = "Bearer";
    //user's detail
    private Long id;
    private String username;
    private List<String> roles;
}
