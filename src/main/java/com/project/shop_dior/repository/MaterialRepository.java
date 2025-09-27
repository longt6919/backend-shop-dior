package com.project.shop_dior.repository;

import com.project.shop_dior.models.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MaterialRepository extends JpaRepository<Material,Long> {
    boolean existsByName(String name);

    @Query("SELECT COUNT(p) > 0 FROM Product p WHERE p.material.id = :materialId")
    boolean existsProductByMaterialId(@Param("materialId") Long materialId);
}
