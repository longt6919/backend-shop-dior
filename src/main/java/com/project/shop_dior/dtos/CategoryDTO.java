package com.project.shop_dior.dtos;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Data
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class CategoryDTO {
    @NotEmpty(message = "Category not empty")
    private String name;
}
