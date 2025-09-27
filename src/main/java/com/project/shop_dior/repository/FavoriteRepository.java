package com.project.shop_dior.repository;

import com.project.shop_dior.models.Favorite;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUserId(Long userId);
    boolean existsByUserIdAndProductId(Long userId, Long productId);
    @Transactional
    void deleteByUserIdAndProductId(Long userId, Long productId);
    Optional<Favorite> findByUserIdAndProductId(Long userId, Long productId);

}
