package com.project.shop_dior.repository;

import com.project.shop_dior.models.Brand;
import com.project.shop_dior.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BrandRepository extends JpaRepository<Brand,Long> {
    boolean existsByName(String name);
    @Query("SELECT b FROM Brand b WHERE b.active = true")
    List<Brand> findAllActive();
}
