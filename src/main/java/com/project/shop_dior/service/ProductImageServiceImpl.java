package com.project.shop_dior.service;

import com.project.shop_dior.models.Product;
import com.project.shop_dior.models.ProductImage;
import com.project.shop_dior.repository.ProductImageRepository;
import com.project.shop_dior.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {
    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;
    @Override
    @Transactional
    public ProductImage deleteProductImage(Long id) {
        Optional<ProductImage> imageOptional = productImageRepository.findById(id);
        if(imageOptional.isEmpty()){
            throw new RuntimeException("Image not found with id: " + id);
        }
        ProductImage productImage = imageOptional.get();
        Product product = productImage.getProduct();
        productImageRepository.deleteById(id);
        if(productImage.getImageUrl().equals(product.getThumbnail())){
            List<ProductImage> remainingImages = productImageRepository.findByProductId(product.getId());
            if (!remainingImages.isEmpty()) {
                // Gán ảnh khác làm thumbnail (lấy ảnh đầu tiên)
                product.setThumbnail(remainingImages.get(0).getImageUrl());
            } else {
                product.setThumbnail(null);
            }
            productRepository.save(product);
        }
        return productImage;
    }
}
