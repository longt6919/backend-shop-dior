package com.project.shop_dior.repository;

import com.project.shop_dior.models.Brand;
import com.project.shop_dior.models.Style;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StyleRepository extends JpaRepository<Style,Long> {
    boolean existsByName(String name);
    @Query("SELECT s FROM Style s WHERE s.active = true")
    List<Style> findAllActive();
}
