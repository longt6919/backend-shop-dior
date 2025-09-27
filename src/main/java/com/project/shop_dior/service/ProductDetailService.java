package com.project.shop_dior.service;

import com.project.shop_dior.dtos.ProductDetailDTO;
import com.project.shop_dior.dtos.UpdateProductDetailDTO;
import com.project.shop_dior.exception.DataNotFoundException;
import com.project.shop_dior.models.Product;
import com.project.shop_dior.models.ProductDetail;
import com.project.shop_dior.responses.ProductDetailResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface ProductDetailService {
    // Lấy danh sách biến thể (product_detail) theo sản phẩm
    List<ProductDetail> getProductDetails(Long productId);

    List<ProductDetail> getAvailableDetails(Long productId);
    // Xoá toàn bộ biến thể của sản phẩm (dùng khi cập nhật lại toàn bộ)
    void deleteAllDetailsOfProduct(Long productId);

    void updateProductDetail(Long productDetailId, UpdateProductDetailDTO updateProductDetailDTO)
            throws DataNotFoundException;
    void updateMinusProductDetail(Long productDetailId, UpdateProductDetailDTO updateProductDetailDTO)
            throws DataNotFoundException;
    List<ProductDetail> getAllDetails(Long productId);
    Page<ProductDetailResponse> searchProductDetails
            (Long productId, String keyword, PageRequest pageRequest);


}
