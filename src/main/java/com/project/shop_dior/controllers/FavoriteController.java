package com.project.shop_dior.controllers;

import com.project.shop_dior.component.JwtTokenUtil;
import com.project.shop_dior.exception.DataNotFoundException;
import com.project.shop_dior.models.Favorite;
import com.project.shop_dior.responses.ResponseObject;
import com.project.shop_dior.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/favorites")
@RequiredArgsConstructor
public class FavoriteController {
    private final FavoriteService favoriteService;
    private final JwtTokenUtil jwtTokenUtil;
    @PostMapping("/{productId}")
    public Favorite addToMyFavorites(@RequestHeader("Authorization") String authorization,
                                     @PathVariable Long productId) throws DataNotFoundException {
        String token = jwtTokenUtil.extractToken(authorization);
        Long userId = jwtTokenUtil.getUserIdFromToken(token);
        return favoriteService.addToFavorites(userId, productId);
    }

    @GetMapping("")
    public List<Favorite> getMyFavorites(@RequestHeader("Authorization") String authorization) {
        String token = jwtTokenUtil.extractToken(authorization);
        Long userId = jwtTokenUtil.getUserIdFromToken(token);
        return favoriteService.getUserFavorites(userId);
    }

    @DeleteMapping("/{productId}")
    public void removeFromMyFavorites(@RequestHeader("Authorization") String authorization,
                                      @PathVariable Long productId) {
        String token = jwtTokenUtil.extractToken(authorization);
        Long userId = jwtTokenUtil.getUserIdFromToken(token);
        favoriteService.removeFromFavorites(userId, productId);
    }
}
