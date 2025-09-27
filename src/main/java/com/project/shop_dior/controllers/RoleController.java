package com.project.shop_dior.controllers;

import com.project.shop_dior.models.Role;
import com.project.shop_dior.service.RoleService;
import com.project.shop_dior.service.RoleServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleServiceImpl;
@GetMapping("")
public ResponseEntity<?>getAllRole(){
    List<Role> roles =roleServiceImpl.getAllRole();
return ResponseEntity.ok(roles);
}

}
