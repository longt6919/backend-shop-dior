package com.project.shop_dior.repository;

import com.project.shop_dior.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product,Long> {
    boolean existsByName(String name);
    Page<Product> findAll(Pageable pageable);//phan trang

    @Query("SELECT p FROM Product p WHERE " +
            "(:categoryId IS NULL OR :categoryId = 0 OR p.category.id = :categoryId) " +
            "AND (:originId IS NULL OR :originId = 0 OR p.origin.id = :originId) " +
            "AND (:materialId IS NULL OR :materialId = 0 OR p.material.id = :materialId) " +
            "AND (:styleId IS NULL OR :styleId = 0 OR p.style.id = :styleId) " +
            "AND (:brandId IS NULL OR :brandId = 0 OR p.brand.id = :brandId) " +
            "AND (" +
            "  :keyword IS NULL OR :keyword = '' OR " +
            "  LOWER(FUNCTION('REPLACE', p.name, ' ', '')) LIKE LOWER(FUNCTION('REPLACE', CONCAT('%', :keyword, '%'), ' ', '')) OR " +
            "  LOWER(FUNCTION('REPLACE', p.description, ' ', '')) LIKE LOWER(FUNCTION('REPLACE', CONCAT('%', :keyword, '%'), ' ', '')) " +
            ")")
    Page<Product> searchProducts(
            @Param("categoryId") Long categoryId,
            @Param("originId") Long originId,
            @Param("materialId") Long materialId,
            @Param("styleId") Long styleId,
            @Param("brandId") Long brandId,
            @Param("keyword") String keyword,
            Pageable pageable);

    @Query("SELECT p FROM Product p WHERE " +
            "(:categoryId   IS NULL OR :categoryId = 0 OR p.category.id = :categoryId) " +
            "AND (:originId  IS NULL OR :originId  = 0 OR p.origin.id   = :originId) " +
            "AND (:materialId IS NULL OR :materialId = 0 OR p.material.id = :materialId) " +
            "AND (:styleId    IS NULL OR :styleId    = 0 OR p.style.id    = :styleId) " +
            "AND (:brandId    IS NULL OR :brandId    = 0 OR p.brand.id    = :brandId) " +
            "AND (" +
            "  :keyword IS NULL OR :keyword = '' OR " +
            "  LOWER(FUNCTION('REPLACE', p.name,        ' ', '')) LIKE LOWER(FUNCTION('REPLACE', CONCAT('%', :keyword, '%'), ' ', '')) OR " +
            "  LOWER(FUNCTION('REPLACE', p.description, ' ', '')) LIKE LOWER(FUNCTION('REPLACE', CONCAT('%', :keyword, '%'), ' ', ''))" +
            ") " +
            "AND p.active = true")
    Page<Product> findByFilters(
            @Param("categoryId") Long categoryId,
            @Param("originId")   Long originId,
            @Param("materialId") Long materialId,
            @Param("styleId")    Long styleId,
            @Param("brandId")    Long brandId,
            @Param("keyword")    String keyword,
            Pageable pageable);




    @Query("select p from Product p left JOIN FETCH p.productImages where p.id = :productId")
    Optional<Product> getProductImage(@Param("productId") Long productId);

    @Query("select p from Product p where p.id in :productIds")
    List<Product> findProductByIds(@Param("productIds") List<Long> productIds);

    @Query("SELECT p FROM Product p WHERE p.active = true AND p.category.id = :categoryId")
    Page<Product> findAllByCategoryId(Long categoryId, Pageable pageable);
    @Query("SELECT p FROM Product p WHERE p.active = true AND p.style.id = :styleId")
    Page<Product> findAllByStyleId(Long styleId, Pageable pageable);
    @Query("SELECT p FROM Product p WHERE p.active = true AND p.brand.id = :brandId")
    Page<Product> findAllByBrandId(Long brandId, Pageable pageable);
    @Query("SELECT p FROM Product p WHERE p.active = true AND(:keyword IS NULL OR p.name LIKE CONCAT('%', :keyword, '%'))")
    Page<Product> searchProductByKeyword(
            @Param("keyword") String keyword,
            Pageable pageable);



}
