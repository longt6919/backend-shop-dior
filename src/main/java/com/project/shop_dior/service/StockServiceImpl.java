package com.project.shop_dior.service;

import com.project.shop_dior.dtos.CartItemCheckDTO;
import com.project.shop_dior.dtos.CartItemRequestDTO;
import com.project.shop_dior.models.ProductDetail;
import com.project.shop_dior.repository.ProductDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService{
    private final ProductDetailRepository productDetailRepository;
    @Override
    @Transactional
    //trả về tồn của 1 pd
    public int getAvailable(Long detailId) {
        return productDetailRepository.findById(detailId).map(productDetail ->
                Math.max(0,productDetail.getQuantity() == null?0:productDetail.getQuantity()))
                .orElse(0);
    }

    @Override
    @Transactional
    public List<CartItemCheckDTO> checkCart(List<CartItemRequestDTO> items) {
        List<CartItemCheckDTO> cartItemChecks = new ArrayList<>();
        if (items == null) return cartItemChecks;

        for (CartItemRequestDTO item : items) {
            int requested = item.getQty() == null ? 0 : item.getQty();
            int available = getAvailable(item.getDetailId());
            boolean ok = requested > 0 && requested <= available;
            cartItemChecks.add(new CartItemCheckDTO(item.getDetailId(), requested, available, ok));
        }
        return cartItemChecks;
    }
    }




