package com.project.shop_dior.service;

import com.project.shop_dior.exception.DataNotFoundException;
import com.project.shop_dior.models.Favorite;

import java.util.List;

public interface FavoriteService {
    Favorite addToFavorites(Long userId, Long productId) throws DataNotFoundException;
    List<Favorite> getUserFavorites(Long userId);
    void removeFromFavorites(Long userId, Long productId);
}
