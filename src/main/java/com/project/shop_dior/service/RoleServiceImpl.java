package com.project.shop_dior.service;

import com.project.shop_dior.models.Role;
import com.project.shop_dior.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    @Override
    public List<Role> getAllRole() {
            return roleRepository.findAll();
        }
    }


