package com.project.shop_dior.service;

import com.project.shop_dior.dtos.CartItemCheckDTO;
import com.project.shop_dior.dtos.CartItemRequestDTO;

import java.util.List;

public interface StockService {
    int getAvailable(Long detailId); //tồn của 1 productDetail
    List<CartItemCheckDTO> checkCart(List<CartItemRequestDTO> items);//nhiều pd

}
