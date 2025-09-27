package com.project.shop_dior.dtos;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OriginDTO {
    @NotEmpty(message = "Origin not empty")
    private String name;
}
