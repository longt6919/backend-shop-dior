package com.project.shop_dior.repository;

import com.project.shop_dior.models.Product;
import com.project.shop_dior.models.ProductDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductDetailRepository extends JpaRepository<ProductDetail,Long> {
    boolean existsByProductAndSizeIdAndColorId(Product product, Long sizeId, Long colorId);
    @Query("SELECT pd FROM ProductDetail pd WHERE pd.product.id = :productId")
    List<ProductDetail> getByProductId(@Param("productId") Long productId);
    Optional<ProductDetail> findByProductIdAndSizeIdAndColorId(
            Long productId,
            Long sizeId,
            Long colorId
    );

    @Query("DELETE FROM ProductDetail pd WHERE pd.product.id = :productId")
    void deleteAllByProductId(@Param("productId") Long productId);

    List<ProductDetail> findByProductIdAndQuantityGreaterThan(Long productId, int minQuantity);

    List<ProductDetail> findByProductId(Long productId);

    @Query("""
    SELECT d FROM ProductDetail d
    WHERE d.product.id = :productId
    AND (
        LOWER(d.color.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(d.size.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
    )
""")
    Page<ProductDetail> findByProductIdAndKeyword(
            @Param("productId") Long productId,
            @Param("keyword") String keyword,
            Pageable pageable
    );
//Ko phải select là là thay đổi dữ liệu
    @Modifying
    @Query("""
     UPDATE ProductDetail pd
        SET pd.quantity = pd.quantity - :qty
      WHERE pd.id = :detailId
        AND pd.quantity >= :qty
  """)
    int updateQuantity(@Param("detailId") Long detailId, @Param("qty") int qty);


}
