package com.project.shop_dior.service;

import com.project.shop_dior.dtos.ProductDTO;
import com.project.shop_dior.dtos.ProductDetailDTO;
import com.project.shop_dior.dtos.ProductImageDTO;
import com.project.shop_dior.exception.DataNotFoundException;
import com.project.shop_dior.models.Product;
import com.project.shop_dior.models.ProductDetail;
import com.project.shop_dior.models.ProductImage;
import com.project.shop_dior.responses.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    // Tạo sản phẩm mới kèm biến thể (product_detail) và ảnh (product_images)
    Product createProduct(ProductDTO productDTO) throws DataNotFoundException;

    // Lấy thông tin chi tiết sản phẩm theo ID
    Product getProductById(long id) throws Exception;

    // Lấy danh sách sản phẩm có tìm kiếm và lọc theo category, phân trang, all detail
    Page<ProductResponse> getAllProductsAdmin(String keyword, Long categoryId,
                                              Long originId, Long brandId, Long styleId, Long materialId, PageRequest pageRequest);

    // Lấy danh sách sản phẩm có tìm kiếm và lọc theo category, phân trang
    Page<ProductResponse> getAllProductsByActive(String keyword, Long categoryId, Long originId,
                                         Long brandId ,Long styleId ,Long materialId, PageRequest pageRequest);

    // Cập nhật sản phẩm chính + biến thể + ảnh
    Product updateProduct(long id, ProductDTO productDTO) throws Exception;

    void deleteProduct(long id);

    Product saveProduct(Product product);

    // Kiểm tra tên sản phẩm đã tồn tại chưa (tránh trùng khi tạo)
    boolean existsByName(String name);

    // Thêm ảnh sản phẩm nếu upload riêng
    ProductImage createProductImage(Long productId, ProductImageDTO productImageDTO) throws Exception;

    // Lấy danh sách sản phẩm theo danh sách ID (dùng cho giỏ hàng, đơn hàng,...)
    List<Product> findProductByIds(List<Long> productIds);

    Page<ProductResponse> getProductsByCategoryId(Long categoryId, PageRequest pageRequest);

    Page<ProductResponse> getProductsByStyleId(Long styleId, PageRequest pageRequest);

    Page<ProductResponse> getProductsByBrandId(Long brandId, PageRequest pageRequest);

    Page<ProductResponse> getProductsByKeyword(String keyword, Pageable pageable);

    void blockOrEnable(Long productId, Boolean active) throws DataNotFoundException;





}
