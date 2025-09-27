package com.project.shop_dior.repository;

import com.project.shop_dior.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category,Long> {
//    @Query("SELECT COUNT(p) > 0 FROM Product p WHERE p.category.id = :categoryId")
//    boolean existsProductByCategoryId(@Param("categoryId") Long categoryId);
boolean existsByName(String name);
    @Query("SELECT c FROM Category c WHERE c.active = true")
List<Category> findAllActive();
}
