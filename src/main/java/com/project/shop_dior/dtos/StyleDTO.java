package com.project.shop_dior.dtos;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StyleDTO {
    @NotEmpty(message = "Style not empty")
    private String name;
}
