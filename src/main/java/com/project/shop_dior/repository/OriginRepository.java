package com.project.shop_dior.repository;

import com.project.shop_dior.models.Origin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OriginRepository extends JpaRepository<Origin,Long> {
    boolean existsByName(String name);
}
