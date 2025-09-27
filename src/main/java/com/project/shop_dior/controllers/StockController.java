package com.project.shop_dior.controllers;

import com.project.shop_dior.dtos.CartItemCheckDTO;
import com.project.shop_dior.dtos.CartItemRequestDTO;
import com.project.shop_dior.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product-details/stock")
public class StockController {
    private final StockService stockService;
    //int tồn
    @GetMapping("/{id}/available")
    public ResponseEntity<Integer> getAvailable(@PathVariable Long id) {
        return ResponseEntity.ok(stockService.getAvailable(id));
    }
    @PostMapping("/cart/check")
    public ResponseEntity<List<CartItemCheckDTO>> checkCart(
            @RequestBody List<CartItemRequestDTO> items) {
        return ResponseEntity.ok(stockService.checkCart(items));
    }
}
