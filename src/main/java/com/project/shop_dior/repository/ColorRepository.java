package com.project.shop_dior.repository;

import com.project.shop_dior.models.Color;
import org.springframework.data.jpa.repository.JpaRepository;



public interface ColorRepository extends JpaRepository<Color,Long> {
    boolean existsByName(String name);
    }


