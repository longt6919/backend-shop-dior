package com.project.shop_dior.responses;

import com.project.shop_dior.models.Role;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleResponse {
private Long id;
private String name;

public static RoleResponse fromRole(Role role){
if (role ==null) return null;
return RoleResponse.builder().id(role.getId()).name(role.getName()).build();
}
}
