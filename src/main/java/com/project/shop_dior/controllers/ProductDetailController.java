package com.project.shop_dior.controllers;

import com.project.shop_dior.dtos.ProductDetailDTO;
import com.project.shop_dior.dtos.UpdateProductDetailDTO;
import com.project.shop_dior.dtos.UpdateUserDTO;
import com.project.shop_dior.exception.DataNotFoundException;
import com.project.shop_dior.responses.ProductDetailListResponse;
import com.project.shop_dior.responses.ProductDetailResponse;
import com.project.shop_dior.responses.ProductListResponse;
import com.project.shop_dior.service.ProductDetailService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/productDetail")
@RequiredArgsConstructor
public class ProductDetailController {
    private final ProductDetailService detailService;
    private final ModelMapper mapper;


    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductDetailDTO>> getAllDetails(
            @PathVariable Long productId
    ) {
        List<ProductDetailDTO> dtos = detailService
                .getProductDetails(productId)
                .stream()
                .map(ProductDetailDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(dtos);
    }
    @PutMapping("/update/{productDetailId}")
    public ResponseEntity<String> updateProductDetailsQuantity(
            @PathVariable Long productDetailId,
            @RequestBody UpdateProductDetailDTO updateProductDetailDTO) throws DataNotFoundException {
        detailService.updateProductDetail(productDetailId, updateProductDetailDTO);
        return ResponseEntity.ok("Nhập hàng thành công!");
    }
    @PutMapping("/update/minus/{productDetailId}")
    public ResponseEntity<String> updateProductDetailsMinusQuantity(
            @PathVariable Long productDetailId,
            @RequestBody UpdateProductDetailDTO updateProductDetailDTO) throws DataNotFoundException {
        detailService.updateMinusProductDetail(productDetailId, updateProductDetailDTO);
        return ResponseEntity.ok("Xóa sản phẩm khỏi kho thành công!");
    }
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDetailListResponse> getAllProductDetails(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit
            ){
        PageRequest pageRequest = PageRequest.of(
                page,limit, Sort.by("id").ascending());
        Page<ProductDetailResponse> productDetailPage = detailService.searchProductDetails(
                productId,keyword,pageRequest);
        int totalPages = productDetailPage.getTotalPages();
        List<ProductDetailResponse> productDetails = productDetailPage.getContent();
        return ResponseEntity.ok(ProductDetailListResponse
                .builder()
                .productDetails(productDetails)
                .totalPages(totalPages)
                .build());
    }

}
