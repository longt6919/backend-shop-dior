package com.project.shop_dior.service;

import com.project.shop_dior.dtos.ProductDTO;
import com.project.shop_dior.dtos.ProductDetailDTO;
import com.project.shop_dior.dtos.ProductImageDTO;
import com.project.shop_dior.exception.DataNotFoundException;
import com.project.shop_dior.exception.InvalidParamException;
import com.project.shop_dior.listeners.ProductChangedEvent;
import com.project.shop_dior.models.*;
import com.project.shop_dior.repository.*;
import com.project.shop_dior.responses.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl  implements ProductService{

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final StyleRepository styleRepository;
    private final MaterialRepository materialRepository;
    private final OriginRepository originRepository;
    private final ProductDetailRepository productDetailRepository;
    private final SizeRepository sizeRepository;
    private final ColorRepository colorRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductDetailService productDetailService;
    private final ApplicationEventPublisher publisher;

    @Override
    @Transactional
    public Product createProduct(ProductDTO productDTO) throws DataNotFoundException {
        // 1. Lấy các tham chiếu (Category, Brand, …)
        Category existingCategory = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new DataNotFoundException("Không thấy danh mục id: " + productDTO.getCategoryId()));
        Brand existingBrand = brandRepository.findById(productDTO.getBrandId())
                .orElseThrow(() -> new DataNotFoundException("Không thấy thương hiệu id: " + productDTO.getBrandId()));
        Style existingStyle = styleRepository.findById(productDTO.getStyleId())
                .orElseThrow(() -> new DataNotFoundException("Không thấy phong cách id: " + productDTO.getStyleId()));
        Origin existingOrigin = originRepository.findById(productDTO.getOriginId())
                .orElseThrow(() -> new DataNotFoundException("Không thấy xuất xứ id: " + productDTO.getOriginId()));
        Material existingMaterial = materialRepository.findById(productDTO.getMaterialId())
                .orElseThrow(() -> new DataNotFoundException("Không thấy chất liệu id: " + productDTO.getMaterialId()));
        if (productDTO.getPrice().compareTo(new BigDecimal("1000")) < 0) {
            throw new IllegalArgumentException("Giá tiền tối thiểu là 1000");
        }
        if (productDTO.getPrice().compareTo(new BigDecimal("10000000")) > 0) {
            throw new IllegalArgumentException("Giá tiền tối đa là 10,000,000");
        }
        // Thêm các validate khác nếu cần
        if (productDTO.getName() == null || productDTO.getName().isBlank()) {
            throw new IllegalArgumentException("Tên sản phẩm không được để trống");
        }
        // 2. Tạo và lưu Product chính
        Product newProduct = Product.builder()
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .thumbnail(productDTO.getThumbnail())
                .description(productDTO.getDescription())
                .category(existingCategory)
                .origin(existingOrigin)
                .style(existingStyle)
                .material(existingMaterial)
                .brand(existingBrand)
                .build();
        Product saved = productRepository.save(newProduct);
        publisher.publishEvent(new ProductChangedEvent(saved.getId()));
// 3. Sinh tự động full size × full color
        List<Size> allSizes = sizeRepository.findAll();
        List<Color> allColors = colorRepository.findAll();
        List<ProductDetail> details = new ArrayList<>();
        for (Size sz : allSizes) {
            for (Color col : allColors) {
                ProductDetail pd = ProductDetail.builder()
                        .product(newProduct)
                        .size(sz)
                        .color(col)
                        .quantity(0)
                        .build();
                details.add(pd);
            }
        }

        // 4. Trả về đối tượng đã lưu, có đầy đủ ID
        return newProduct;
    }


    @Override
    public Product getProductById(long id) throws Exception {
        return productRepository.findById(id).orElseThrow(()
                ->new DataNotFoundException("Không tìm thấy sản phẩm id ="+ id));
    }

//    @Override
//    public Page<ProductResponse> getAllProducts(String keyword, Long categoryId,
//           Long originId, Long brandId, Long styleId, Long materialId, PageRequest pageRequest) {
//        Page<Product> products = productRepository.searchProducts(brandId,categoryId,originId,styleId,materialId,keyword,pageRequest);
//        return products.map(product -> {
//            // 1) Lấy luôn detail có quantity>0
//            List<ProductDetailDTO> detailDTOs = productDetailService
//                    .getAvailableDetails(product.getId())           // trả về chỉ quantity>0
//                    .stream()
//                    .map(ProductDetailDTO::fromEntity)
//                    .toList();                                      // hoặc .collect(Collectors.toList())
//            // 2) Map Product + detailDTOs vào response
//            return ProductResponse.builder()
//                    .id(product.getId())
//                    .name(product.getName())
//                    .price(product.getPrice())
//                    .thumbnail(product.getThumbnail())
//                    .description(product.getDescription())
//                    .categoryId(product.getCategory().getId())
//                    .originId(product.getOrigin().getId())
//                    .brandId(product.getBrand().getId())
//                    .styleId(product.getStyle().getId())
//                    .materialId(product.getMaterial().getId())
//                    .productImages(product.getProductImages())
//                    .active(product.isActive())
//                    .createAt(product.getCreateAt())
//                    .updateAt(product.getUpdateAt())
//                        // <-- Không cần khai báo lại, cứ gọi builder bình thường
//                    // … map thêm các trường khác tuỳ ProductResponse …
//                    .productDetails(detailDTOs)
//                    .build();
//        });
//    }

    @Override
    public Page<ProductResponse> getAllProductsAdmin(String keyword, Long categoryId,
                                                Long originId, Long brandId, Long styleId, Long materialId, PageRequest pageRequest) {
        Page<Product> products = productRepository.searchProducts(brandId,categoryId,originId,styleId,materialId,keyword,pageRequest);
        return products.map(product -> {
            List<ProductDetailDTO> detailDTOs = productDetailService
                    .getAllDetails(product.getId())      // lấy tất cả (không lọc)
                    .stream()
                    .map(ProductDetailDTO::fromEntity)
                    .toList();
            // hoặc .collect(Collectors.toList())
            // 2) Map Product + detailDTOs vào response
            return ProductResponse.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .price(product.getPrice())
                    .thumbnail(product.getThumbnail())
                    .description(product.getDescription())
                    .categoryId(product.getCategory().getId())
                    .originId(product.getOrigin().getId())
                    .brandId(product.getBrand().getId())
                    .styleId(product.getStyle().getId())
                    .materialId(product.getMaterial().getId())
                    .productImages(product.getProductImages())
                    .active(product.isActive())
                    .createAt(product.getCreateAt())
                    .updateAt(product.getUpdateAt())
                    // <-- Không cần khai báo lại, cứ gọi builder bình thường
                    // … map thêm các trường khác tuỳ ProductResponse …
                    .productDetails(detailDTOs)
                    .build();
        });
    }


    @Override
    public Page<ProductResponse> getAllProductsByActive(String keyword, Long categoryId,
           Long originId, Long brandId, Long styleId, Long materialId, PageRequest pageRequest) {
        Page<Product> products = productRepository.findByFilters(brandId,categoryId,originId,styleId,materialId,keyword,pageRequest);
        return products.map(product -> {
            // 1) Lấy luôn detail có quantity>0
            List<ProductDetailDTO> detailDTOs = productDetailService
                    .getAvailableDetails(product.getId())           // trả về chỉ quantity>0
                    .stream()
                    .map(ProductDetailDTO::fromEntity)
                    .toList();                                      // hoặc .collect(Collectors.toList())
            // 2) Map Product + detailDTOs vào response
            return ProductResponse.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .price(product.getPrice())
                    .thumbnail(product.getThumbnail())
                    .description(product.getDescription())
                    .categoryId(product.getCategory().getId())
                    .originId(product.getOrigin().getId())
                    .brandId(product.getBrand().getId())
                    .styleId(product.getStyle().getId())
                    .materialId(product.getMaterial().getId())
                    .productImages(product.getProductImages())
                    .active(product.isActive())
                    .createAt(product.getCreateAt())
                    .updateAt(product.getUpdateAt())
                    // <-- Không cần khai báo lại, cứ gọi builder bình thường
                    // … map thêm các trường khác tuỳ ProductResponse …
                    .productDetails(detailDTOs)
                    .build();
        });
    }


    @Override
    @Transactional
    public Product updateProduct(long id, ProductDTO productDTO) throws Exception {
        Product existingProduct = getProductById(id);
        if(existingProduct !=null){
            Category existingCategory = categoryRepository
                    .findById(productDTO.getCategoryId())
                    .orElseThrow(() ->
                            new DataNotFoundException("Cannot find category with id: "+productDTO.getCategoryId()));
            Brand existingBrand = brandRepository
                    .findById(productDTO.getBrandId())
                    .orElseThrow(() ->
                            new DataNotFoundException("Cannot find brand with id: "+productDTO.getBrandId()));
            Style existingStyle = styleRepository
                    .findById(productDTO.getStyleId())
                    .orElseThrow(() ->
                            new DataNotFoundException("Cannot find style with id: "+productDTO.getStyleId()));
            Origin existingOrigin = originRepository
                    .findById(productDTO.getOriginId())
                    .orElseThrow(() ->
                            new DataNotFoundException("Cannot find origin with id: "+productDTO.getOriginId()));
            Material existingMaterial = materialRepository
                    .findById(productDTO.getMaterialId())
                    .orElseThrow(() ->
                            new DataNotFoundException("Cannot find material with id: "+productDTO.getMaterialId()));
            if (productDTO.getPrice().compareTo(new BigDecimal("1000")) < 0) {
                throw new DataNotFoundException("Giá tiền tối thiểu là 1000");
            }
            if (productDTO.getPrice().compareTo(new BigDecimal("10000000")) > 0) {
                throw new DataNotFoundException("Giá tiền tối đa là 10,000,000");
            }
            // Thêm các validate khác nếu cần
            if (productDTO.getName() == null || productDTO.getName().isBlank()) {
                throw new DataNotFoundException("Tên sản phẩm không được để trống");
            }
            existingProduct.setName(productDTO.getName());
            existingProduct.setCategory(existingCategory);
            existingProduct.setBrand(existingBrand);
            existingProduct.setStyle(existingStyle);
            existingProduct.setOrigin(existingOrigin);
            existingProduct.setMaterial(existingMaterial);
            existingProduct.setPrice(productDTO.getPrice());
            existingProduct.setDescription(productDTO.getDescription());
            //  Chỉ update thumbnail nếu được truyền từ frontend
            if (productDTO.getThumbnail() != null && !productDTO.getThumbnail().isEmpty()) {
                existingProduct.setThumbnail(productDTO.getThumbnail());
            }  Product saved = productRepository.save(existingProduct);
            publisher.publishEvent(new ProductChangedEvent(saved.getId()));
            return saved;
        }
        return null;
    }

    @Override
    @Transactional
    public void deleteProduct(long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        optionalProduct.ifPresent(productRepository::delete);
    }

    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public boolean existsByName(String name) {
        return productRepository.existsByName(name);
    }

    @Override
    @Transactional
    public ProductImage createProductImage(Long productId, ProductImageDTO productImageDTO) throws Exception {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(()->new DataNotFoundException("Cannot find product with id: "+productImageDTO.getProductId()));
        ProductImage newProductImage = ProductImage.builder()
                .product(existingProduct)
                .imageUrl(productImageDTO.getImageUrl())
                .build();
        //ko cho insert qua 5 anh cho 1 san pham
        int size = Optional.ofNullable(productImageRepository.findByProductId(productId))
                .map(List::size)
                .orElse(0);
        if(size>=5){
            throw new InvalidParamException("Số lượng ảnh trên 1 sản phẩm tối đa 5");
        }
        return productImageRepository.save(newProductImage);
    }

    @Override
    public List<Product> findProductByIds(List<Long> productIds) {
        return productRepository.findProductByIds(productIds);
    }

    @Override
    public Page<ProductResponse> getProductsByCategoryId(Long categoryId, PageRequest pageRequest) {
        Page<Product> products = productRepository.findAllByCategoryId(categoryId, pageRequest);

        return products.map(product -> {
            List<ProductDetailDTO> detailDTOs = productDetailService
                    .getAvailableDetails(product.getId())
                    .stream()
                    .map(ProductDetailDTO::fromEntity)
                    .toList();

            return ProductResponse.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .price(product.getPrice())
                    .thumbnail(product.getThumbnail())
                    .description(product.getDescription())
                    .categoryId(product.getCategory().getId())
                    .originId(product.getOrigin().getId())
                    .brandId(product.getBrand().getId())
                    .brandName(product.getBrand().getName())
                    .styleId(product.getStyle().getId())
                    .materialId(product.getMaterial().getId())
                    .productImages(product.getProductImages())
                    .active(product.isActive())
                    .createAt(product.getCreateAt())
                    .updateAt(product.getUpdateAt())
                    .productDetails(detailDTOs)
                    .build();
        });
    }

    @Override
    public Page<ProductResponse> getProductsByStyleId(Long styleId, PageRequest pageRequest) {
        Page<Product> products = productRepository.findAllByStyleId(styleId, pageRequest);

        return products.map(product -> {
            List<ProductDetailDTO> detailDTOs = productDetailService
                    .getAvailableDetails(product.getId())
                    .stream()
                    .map(ProductDetailDTO::fromEntity)
                    .toList();

            return ProductResponse.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .price(product.getPrice())
                    .thumbnail(product.getThumbnail())
                    .description(product.getDescription())
                    .categoryId(product.getCategory().getId())
                    .originId(product.getOrigin().getId())
                    .brandId(product.getBrand().getId())
                    .styleId(product.getStyle().getId())
                    .materialId(product.getMaterial().getId())
                    .productImages(product.getProductImages())
                    .active(product.isActive())
                    .createAt(product.getCreateAt())
                    .updateAt(product.getUpdateAt())
                    .productDetails(detailDTOs)
                    .build();
        });
    }

    @Override
    public Page<ProductResponse> getProductsByBrandId(Long brandId, PageRequest pageRequest) {
        Page<Product> products = productRepository.findAllByBrandId(brandId, pageRequest);

        return products.map(product -> {
            List<ProductDetailDTO> detailDTOs = productDetailService
                    .getAvailableDetails(product.getId())
                    .stream()
                    .map(ProductDetailDTO::fromEntity)
                    .toList();

            return ProductResponse.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .price(product.getPrice())
                    .thumbnail(product.getThumbnail())
                    .description(product.getDescription())
                    .categoryId(product.getCategory().getId())
                    .originId(product.getOrigin().getId())
                    .brandId(product.getBrand().getId())
                    .styleId(product.getStyle().getId())
                    .materialId(product.getMaterial().getId())
                    .productImages(product.getProductImages())
                    .active(product.isActive())
                    .createAt(product.getCreateAt())
                    .updateAt(product.getUpdateAt())
                    .productDetails(detailDTOs)
                    .build();
        });    }

    @Override
    public Page<ProductResponse> getProductsByKeyword(String keyword, Pageable pageable) {
        Page<Product> products = productRepository.searchProductByKeyword(keyword,pageable);

        return products.map(product -> {
            List<ProductDetailDTO> detailDTOs = productDetailService
                    .getAvailableDetails(product.getId())
                    .stream()
                    .map(ProductDetailDTO::fromEntity)
                    .toList();

            return ProductResponse.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .price(product.getPrice())
                    .thumbnail(product.getThumbnail())
                    .description(product.getDescription())
                    .categoryId(product.getCategory().getId())
                    .originId(product.getOrigin().getId())
                    .brandId(product.getBrand().getId())
                    .styleId(product.getStyle().getId())
                    .materialId(product.getMaterial().getId())
                    .productImages(product.getProductImages())
                    .active(product.isActive())
                    .createAt(product.getCreateAt())
                    .updateAt(product.getUpdateAt())
                    .productDetails(detailDTOs)
                    .build();
        });
    }

    @Override
    @Transactional
    public void blockOrEnable(Long productId, Boolean active) throws DataNotFoundException {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(()-> new DataNotFoundException("Product not found"));
        existingProduct.setActive(active);
        Product saved = productRepository.save(existingProduct);
        publisher.publishEvent(new ProductChangedEvent(saved.getId()));
    }
}
