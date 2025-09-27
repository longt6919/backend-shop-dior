package com.project.shop_dior.controllers;

import com.project.shop_dior.models.ProductImage;
import com.project.shop_dior.responses.ResponseObject;
import com.project.shop_dior.service.ProductImageService;
import com.project.shop_dior.service.ProductService;
import com.project.shop_dior.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/products/product_images")
@RequiredArgsConstructor
public class ProductImageController {
    private final ProductImageService productImageService;
    private final ProductService productService;
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<ResponseObject> delete(
            @PathVariable Long id
    ) throws Exception {
        ProductImage productImage = productImageService.deleteProductImage(id);
        if(productImage != null){
            FileUtils.deleteFile(productImage.getImageUrl());
        }
        return ResponseEntity.ok().body(
                ResponseObject.builder()
                        .message("Delete product image successfully")
                        .data(productImage)
                        .status(HttpStatus.OK)
                        .build()
        );
    }
}
