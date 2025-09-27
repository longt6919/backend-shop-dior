package com.project.shop_dior.service;

import com.project.shop_dior.dtos.ProductDTO;
import com.project.shop_dior.dtos.ProductDetailDTO;
import com.project.shop_dior.dtos.UpdateProductDetailDTO;
import com.project.shop_dior.exception.DataNotFoundException;
import com.project.shop_dior.models.Color;
import com.project.shop_dior.models.Product;
import com.project.shop_dior.models.ProductDetail;
import com.project.shop_dior.models.Size;
import com.project.shop_dior.repository.ColorRepository;
import com.project.shop_dior.repository.ProductDetailRepository;
import com.project.shop_dior.repository.ProductRepository;
import com.project.shop_dior.repository.SizeRepository;
import com.project.shop_dior.responses.ProductDetailResponse;
import com.project.shop_dior.responses.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductDetailServiceImpl implements ProductDetailService{
    private final ProductDetailRepository productDetailRepository;

    @Override
    public List<ProductDetail> getProductDetails(Long productId) {
        return productDetailRepository.getByProductId(productId);
    }

    @Override
    public List<ProductDetail> getAvailableDetails(Long productId) {
        return productDetailRepository.findByProductIdAndQuantityGreaterThan(productId,0);
    }

    @Override
    public void deleteAllDetailsOfProduct(Long productId) {
productDetailRepository.deleteAllByProductId(productId);
    }

    @Override
    @Transactional
    public void updateProductDetail(Long productDetailId, UpdateProductDetailDTO updateProductDetailDTO) throws DataNotFoundException {
        ProductDetail productDetail = productDetailRepository.findById(productDetailId)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy biến thể với id: " + productDetailId));
        int numberProduct = updateProductDetailDTO.getQuantity();
        int newQuantity = productDetail.getQuantity() + numberProduct;
        productDetail.setQuantity(newQuantity);
        productDetailRepository.save(productDetail);
    }

    @Override
    @Transactional
    public void updateMinusProductDetail(Long productDetailId, UpdateProductDetailDTO updateProductDetailDTO) throws DataNotFoundException {
        ProductDetail productDetail = productDetailRepository.findById(productDetailId)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy biến thể với id: " + productDetailId));
        int numberProduct = updateProductDetailDTO.getQuantity();
        int currentQuantity = productDetail.getQuantity();
        if (numberProduct > currentQuantity) {
            throw new IllegalArgumentException("Số lượng tồn không đủ. Hiện còn " + currentQuantity);
        }
        int newQuantity = productDetail.getQuantity() - numberProduct;
        productDetail.setQuantity(newQuantity);
        productDetailRepository.save(productDetail);
    }

    @Override
    public List<ProductDetail> getAllDetails(Long productId) {
        return productDetailRepository.findByProductId(productId);
    }

    @Override
    public Page<ProductDetailResponse> searchProductDetails
            (Long productId, String keyword, PageRequest pageRequest) {
        Page<ProductDetail> productDetails = productDetailRepository.findByProductIdAndKeyword(productId,keyword,pageRequest);
      return productDetails.map(ProductDetailResponse::fromEntity);
    }
}

