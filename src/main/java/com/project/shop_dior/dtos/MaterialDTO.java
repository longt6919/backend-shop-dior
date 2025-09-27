package com.project.shop_dior.dtos;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaterialDTO {
    @NotEmpty(message = "Material not empty")
    private String name;
}
