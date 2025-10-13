package com.project.shop_dior.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.shop_dior.component.LocalizationUtils;
import com.project.shop_dior.dtos.ProductDTO;
import com.project.shop_dior.dtos.ProductDetailDTO;
import com.project.shop_dior.dtos.ProductImageDTO;
import com.project.shop_dior.exception.DataNotFoundException;
import com.project.shop_dior.models.Product;
import com.project.shop_dior.models.ProductDetail;
import com.project.shop_dior.models.ProductImage;
import com.project.shop_dior.responses.ProductListResponse;
import com.project.shop_dior.responses.ProductResponse;
import com.project.shop_dior.responses.ResponseObject;
import com.project.shop_dior.service.ProductDetailService;
import com.project.shop_dior.service.ProductRedisService;
import com.project.shop_dior.service.ProductService;
import com.project.shop_dior.utils.FileUtils;
import com.project.shop_dior.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/products")
@Validated
@RequiredArgsConstructor
public class ProductController {
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    private final ProductService productService;
    private final ProductDetailService productDetailService;
    private final ProductRedisService productRedisService;
    private final LocalizationUtils localizationUtils;
    @GetMapping("/home/admin")
    public ResponseEntity<ProductListResponse> getAllProductsAdmin(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0", name = "category_id") Long categoryId,
            @RequestParam(defaultValue = "0", name = "brand_id") Long brandId,
            @RequestParam(defaultValue = "0", name = "origin_id") Long originId,
            @RequestParam(defaultValue = "0", name = "style_id") Long styleId,
            @RequestParam(defaultValue = "0", name = "material_id") Long materialId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int limit
    ) throws JsonProcessingException {

        int totalPages = 0;
        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by("id").ascending());

        // Ghi log tham số
        logger.info("getAllProductsAdmin: keyword={}, category={}, brand={}, origin={}, page={}, limit={}",
                keyword, categoryId, brandId, originId, page, limit);

        // ✅ 1) Kiểm tra cache
        List<ProductResponse> productResponses = productRedisService.getAllProducts(
                keyword, categoryId, originId, brandId, styleId, materialId, pageRequest);

        if (productResponses != null) {
            logger.info("✅ [CACHE HIT] Dữ liệu lấy từ Redis cache");
            if (!productResponses.isEmpty()) totalPages = productResponses.get(0).getTotalPages();
        } else {
            logger.warn("❌ [CACHE MISS] Không có trong cache, đang truy vấn DB...");
            // 2) Query DB
            Page<ProductResponse> productPage = productService.getAllProductsAdmin(
                    keyword, categoryId, brandId, originId, styleId, materialId, pageRequest);
            totalPages = productPage.getTotalPages();
            productResponses = productPage.getContent();

            for (ProductResponse product : productResponses) {
                product.setTotalPages(totalPages);
            }
            // 3) Save cache
            productRedisService.saveAllProducts(
                    productResponses, keyword, categoryId, originId, brandId, styleId, materialId, pageRequest);
            logger.info("💾 [CACHE SAVE] Đã lưu {} sản phẩm vào Redis", productResponses.size());
        }
        return ResponseEntity.ok(ProductListResponse.builder()
                .products(productResponses)
                .totalPages(totalPages)
                .build());
    }

    @GetMapping("")
    public ResponseEntity<ProductListResponse> getAllProductsByActive(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0",name = "category_id") Long categoryId,
            @RequestParam(defaultValue = "0",name = "brand_id") Long brandId,
            @RequestParam(defaultValue = "0",name = "origin_id") Long originId,
            @RequestParam(defaultValue = "0",name = "style_id") Long styleId,
            @RequestParam(defaultValue = "0",name = "material_id") Long materialId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int limit
    )throws JsonProcessingException {

        int totalPages = 0;
        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by("id").ascending());

        // Ghi log tham số
        logger.info("getAllProductsAdmin: keyword={}, category={}, brand={}, origin={}, page={}, limit={}",
                keyword, categoryId, brandId, originId, page, limit);

        // 1) Kiểm tra cache
        List<ProductResponse> productResponses = productRedisService.getAllProductsByActive(
                keyword, categoryId, originId, brandId, styleId, materialId, pageRequest);

        if (productResponses != null) {
            logger.info("✅ [CACHE HIT] Dữ liệu lấy từ Redis cache");
            if (!productResponses.isEmpty()) totalPages = productResponses.get(0).getTotalPages();
        } else {
            logger.warn("❌ [CACHE MISS] Không có trong cache, đang truy vấn DB...");
            // 2) Query DB
            Page<ProductResponse> productPage = productService.getAllProductsByActive(
                    keyword, categoryId, brandId, originId, styleId, materialId, pageRequest);
            totalPages = productPage.getTotalPages();
            productResponses = productPage.getContent();
            for (ProductResponse product : productResponses) {
                product.setTotalPages(totalPages);
            }
            // 3) Save cache
            productRedisService.saveAllProducts(
                    productResponses, keyword, categoryId, originId, brandId, styleId, materialId, pageRequest);
            logger.info("💾 [CACHE SAVE] Đã lưu {} sản phẩm vào Redis", productResponses.size());
        }
        return ResponseEntity.ok(ProductListResponse.builder()
                .products(productResponses)
                .totalPages(totalPages)
                .build());
    }
    @GetMapping("images/{imageName}")
    public ResponseEntity<?> viewImage(@PathVariable String imageName) {
        try {
            java.nio.file.Path imagePath = Paths.get("uploads/" + imageName);
            UrlResource resource = new UrlResource(imagePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(FileUtils.getMediaType(imageName))
                        .body(resource);
            } else {
                // fallback ảnh nếu ảnh chính không tồn tại
                UrlResource fallback = new UrlResource(Paths.get("uploads/notfound.png").toUri());
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_PNG)
                        .body(fallback);
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    @PostMapping("")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<ResponseObject> createProduct(
            @Valid @RequestBody ProductDTO productDTO,
            BindingResult result)
            throws Exception{
        if (result.hasErrors()){
            List<String> errorMessages = result.getFieldErrors().stream().map(FieldError::getDefaultMessage).toList();
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message(String.join("; ",errorMessages)).status(HttpStatus.BAD_REQUEST).build());
        }
        Product newProduct = productService.createProduct(productDTO);
        // 2. Lấy lại detail
        List<ProductDetail> details = productDetailService.getProductDetails(newProduct.getId());
        // 3. Map thành List<ProductDetailDTO>
        List<ProductDetailDTO> detailDTOs = details.stream()
                .map(d -> ProductDetailDTO.builder()
                        .sizeId(d.getSize().getId())
                        .colorId(d.getColor().getId())
                        .quantity(d.getQuantity())
                        .build())
                .toList();
//         4. Map Product → ProductDTO (response)
        ProductDTO resp = ProductDTO.builder()
                .name(newProduct.getName())
                .price(newProduct.getPrice())
                .thumbnail(newProduct.getThumbnail())
                .description(newProduct.getDescription())
                .categoryId(newProduct.getCategory().getId())
                .brandId(newProduct.getBrand().getId())
                .styleId(newProduct.getStyle().getId())
                .originId(newProduct.getOrigin().getId())
                .materialId(newProduct.getMaterial().getId())
                .productDetails(detailDTOs)
                .build();
        return ResponseEntity.ok(ResponseObject.builder().message("Create new product successfully")
                .status(HttpStatus.CREATED).data(newProduct).build());
    }

    @PostMapping(value = "/uploads/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<ResponseObject> uploadImages(
            @PathVariable("id") Long productId,
            @RequestParam("files") List<MultipartFile> files
    ) throws Exception {
        Product exitingProduct = productService.getProductById(productId);
        files = files ==null? new ArrayList<MultipartFile>():files;
        if(files.size()>ProductImage.MAXIMUM_IMAGES_PER_PRODUCT){
            return ResponseEntity.badRequest().body(
                    ResponseObject.builder().message(localizationUtils
                            .getLocalizedMessage(MessageKeys.UPLOAD_IMAGES_MAX_5)).build()
            );
        }
        List<ProductImage> productImages = new ArrayList<>();
        for (MultipartFile file : files){
            if(file.getSize()==0){
                continue;
            }
            if(file.getSize()>10*1024*1024){
                return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                        .body(ResponseObject.builder().message(localizationUtils.getLocalizedMessage(MessageKeys.UPLOAD_IMAGES_FILE_LARGE))
                                .status(HttpStatus.PAYLOAD_TOO_LARGE).build());
            }
            String filename = FileUtils.storeFile(file, "uploads");
            ProductImage productImage = productService.createProductImage(
                    exitingProduct.getId(),
                    ProductImageDTO.builder()
                            .imageUrl(filename).build()
            );
            productImages.add(productImage);
        }
        if (!productImages.isEmpty()) {
            ProductImage firstImage = productImages.get(0);
            exitingProduct.setThumbnail(firstImage.getImageUrl()); // Gán thumbnail
            productService.saveProduct(exitingProduct);            // Lưu lại product
        }
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Upload image successfully")
                .status(HttpStatus.CREATED).data(productImages).build());
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(
            @PathVariable("id") Long productId
    ) {
        try {
            // 1) Lấy product
            Product product = productService.getProductById(productId);
            // 2) Lấy list detail còn hàng và map sang DTO
            List<ProductDetailDTO> details = productDetailService
                    .getAvailableDetails(productId)  // chỉ quantity > 0
                    .stream()
                    .map(ProductDetailDTO::fromEntity)
                    .toList();
            // 3) Build ProductResponse đã có factory nhận thêm details
            ProductResponse resp = ProductResponse.fromProduct(product, details);
            // 4) Trả về
            return ResponseEntity.ok(resp);

        } catch (Exception e) {
            // Nếu có lỗi (ví dụ không tìm thấy), trả về message
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<ResponseObject> deleteProduct(@PathVariable long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ResponseObject.builder().data(null)
                .message(String.format("Product with id = %d deleted successfully", id)).status(HttpStatus.OK).build());
    }
    //find theo id tùy số lượng http://localhost:8080/api/v1/products/by-ids?ids=1,2,3
    @GetMapping("/by-ids")
    public ResponseEntity<?> getProductByIds(@RequestParam("ids") String ids){
        try{
            List<Long> productIds = Arrays.stream(ids.split(","))
                    .map(Long::parseLong).collect(Collectors.toList());
            List<Product> products = productService.findProductByIds(productIds);
            return ResponseEntity.ok(products);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<?> updateProduct(
            @PathVariable long id,
            @RequestBody ProductDTO productDTO) throws Exception {
            Product updatedProduct = productService.updateProduct(id, productDTO);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .message("Cập nhật sản phẩm thành công")
                        .status(HttpStatus.OK)
                        .data(updatedProduct)
                        .build()
        );

    }
    @GetMapping("/details/{id}")
    public ResponseEntity<List<ProductDetailDTO>> getDetails(@PathVariable Long id) {
        List<ProductDetail> details = productDetailService.getProductDetails(id);
        List<ProductDetailDTO> dtos = details.stream()
                .map(ProductDetailDTO::fromEntity)  // hoặc build thủ công
                .toList();
        return ResponseEntity.ok(dtos);
    }


    // Xóa hết detail (nếu cần)
    @DeleteMapping("/details/{id}")
    public ResponseEntity<Void> deleteAllDetails(@PathVariable Long id) {
        productDetailService.deleteAllDetailsOfProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-category")
    public ResponseEntity<Page<ProductResponse>> getProductsByCategory(
            @RequestParam Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int limit
    ) {
        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by("id").descending());
        Page<ProductResponse> result = productService.getProductsByCategoryId(categoryId, pageRequest);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/by-style")
    public ResponseEntity<Page<ProductResponse>> getProductsByStyle(
            @RequestParam Long styleId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int limit
    ) {
        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by("id").descending());
        Page<ProductResponse> result = productService.getProductsByStyleId(styleId, pageRequest);
        return ResponseEntity.ok(result);
    }
    @GetMapping("/by-brand")
    public ResponseEntity<Page<ProductResponse>> getProductsByBrand(
            @RequestParam Long brandId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int limit
    ) {
        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by("id").descending());
        Page<ProductResponse> result = productService.getProductsByBrandId(brandId, pageRequest);
        return ResponseEntity.ok(result);
    }
    @GetMapping("/by-keyword")
    public ResponseEntity<Page<ProductResponse>> getProductsByKeyword(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int limit
    ) {
        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by("id").descending());
        Page<ProductResponse> result = productService.getProductsByKeyword(keyword, pageRequest);
        return ResponseEntity.ok(result);
    }
    @PutMapping("/block/{productId}/{active}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<String> blockOrEnable(
            @Valid @PathVariable long productId,
            @Valid @PathVariable int active
    ) {
        try {
            productService.blockOrEnable(productId, active > 0);
            String message = active > 0 ? "Successfully enabled product." : "Successfully blocked the product.";
            return ResponseEntity.ok().body(message);
        } catch (DataNotFoundException e) {
            return ResponseEntity.badRequest().body("Product not found.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
