package com.project.shop_dior.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.shop_dior.responses.ProductResponse;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface ProductRedisService {
    void clear(); //xoá cache
    void clearCacheProduct();
    void clearCacheProductActive();
    List<ProductResponse> getAllProductsByActive(String keyword, Long categoryId, Long originId,
                                         Long brandId , Long styleId , Long materialId, PageRequest pageRequest) throws JsonProcessingException;
    List<ProductResponse> getAllProducts(String keyword, Long categoryId, Long originId,
                                                 Long brandId , Long styleId , Long materialId, PageRequest pageRequest) throws JsonProcessingException;
void saveAllProducts( List<ProductResponse> productResponse,String keyword, Long categoryId, Long originId,
                      Long brandId , Long styleId , Long materialId, PageRequest pageRequest) throws JsonProcessingException;
}
