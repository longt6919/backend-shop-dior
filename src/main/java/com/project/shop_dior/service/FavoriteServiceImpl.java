package com.project.shop_dior.service;

import com.project.shop_dior.exception.DataNotFoundException;
import com.project.shop_dior.models.Favorite;
import com.project.shop_dior.models.Product;
import com.project.shop_dior.models.User;
import com.project.shop_dior.repository.FavoriteRepository;
import com.project.shop_dior.repository.ProductRepository;
import com.project.shop_dior.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService{
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    public Favorite addToFavorites(Long userId, Long productId) throws DataNotFoundException {
        if (favoriteRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new DataNotFoundException("Sản phẩm đã có trong danh sách yêu thích");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy user"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setProduct(product);
        return favoriteRepository.save(favorite);    }

    @Override
    public List<Favorite> getUserFavorites(Long userId) {
        return favoriteRepository.findByUserId(userId);    }

    @Override
    @Transactional
    public void removeFromFavorites(Long userId, Long productId) {
        favoriteRepository.deleteByUserIdAndProductId(userId, productId);
    }
}
