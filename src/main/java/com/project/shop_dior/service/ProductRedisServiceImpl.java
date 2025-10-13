package com.project.shop_dior.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.shop_dior.controllers.ProductController;
import com.project.shop_dior.responses.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProductRedisServiceImpl implements ProductRedisService{
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper redisObjectMapper;
    @Value("${spring.data.redis.use-redis-cache:false}")
    private boolean useRedisCache;
    private String getKeyFrom(String keyword,
                              Long categoryId,
                              Long originId,
                              Long brandId ,Long styleId ,Long materialId,
                              PageRequest pageRequest) {
        int pageNumber = pageRequest.getPageNumber();
        int pageSize = pageRequest.getPageSize();
        Sort sort = pageRequest.getSort();
        String sortDirection = Objects.requireNonNull(sort.getOrderFor("id"))
                .getDirection() == Sort.Direction.ASC ? "asc" : "desc";
        String key = String.format("all_products:%s:%d:%d:%d:%d:%d:%d:%d:%s",
                keyword, categoryId, originId,
                brandId, styleId, materialId, pageNumber,
                pageSize, sortDirection);
        return key;
    }
    private String getKeyFromActive(String keyword,
                              Long categoryId,
                              Long originId,
                              Long brandId ,Long styleId ,Long materialId,
                              PageRequest pageRequest) {
        int pageNumber = pageRequest.getPageNumber();
        int pageSize = pageRequest.getPageSize();
        Sort sort = pageRequest.getSort();
        String sortDirection = Objects.requireNonNull(sort.getOrderFor("id"))
                .getDirection() == Sort.Direction.ASC ? "asc" : "desc";
        String key = String.format("all_products_active:%s:%d:%d:%d:%d:%d:%d:%d:%s",
                keyword, categoryId, originId,
                brandId, styleId, materialId, pageNumber,
                pageSize, sortDirection);
        return key;
    }
// cái này là xoá toàn bộ cache kể cả order,product,user... Hạn chế sử dụng
    @Override
    public void clear() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    //cái này xoá riêng products (all_products) cái này là lấy từ method getKeyFrom (String key = String.format
    // ("all_products:%s:%d:%d:%d:%d:%d:%d:%d:%s",..)
    @Override
    public void clearCacheProduct() {
        String pattern = "all_products:*";
        ScanOptions options = ScanOptions.scanOptions().match(pattern).count(200).build();

        try (var connection = Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection();
             Cursor<byte[]> cursor = connection.scan(options)) {

            int deletedCount = 0;
            while (cursor.hasNext()) {
                byte[] key = cursor.next();
                redisTemplate.delete(new String(key));
                deletedCount++;
            }
            logger.info("[CACHE CLEAR] Đã xoá {} key trong Redis (pattern: {})", deletedCount, pattern);
        } catch (Exception e) {
            logger.error("[CACHE CLEAR ERROR]", e);
            throw new RuntimeException("Lỗi khi xóa cache sản phẩm", e);
        }
    }
    @Override
    public void clearCacheProductActive() {
        String pattern = "all_products_active:*";
        ScanOptions options = ScanOptions.scanOptions().match(pattern).count(200).build();

        try (var connection = Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection();
             Cursor<byte[]> cursor = connection.scan(options)) {

            int deletedCount = 0;
            while (cursor.hasNext()) {
                byte[] key = cursor.next();
                redisTemplate.delete(new String(key));
                deletedCount++;
            }
            logger.info("[CACHE CLEAR] Đã xoá {} key trong Redis (pattern: {})", deletedCount, pattern);
        } catch (Exception e) {
            logger.error("[CACHE CLEAR ERROR]", e);
            throw new RuntimeException("Lỗi khi xóa cache sản phẩm", e);
        }
    }

    @Override
    public List<ProductResponse> getAllProductsByActive(String keyword, Long categoryId, Long originId, Long brandId, Long styleId, Long materialId, PageRequest pageRequest) throws JsonProcessingException {
        if (!useRedisCache) {
            return null;
        }
        String key = this.getKeyFromActive(keyword, categoryId, originId, brandId, styleId, materialId, pageRequest);
        String json = (String) redisTemplate.opsForValue().get(key);
        List<ProductResponse> productResponses =
                json != null ? redisObjectMapper.readValue(json, new TypeReference<List<ProductResponse>>() {}): null;
        return productResponses;     }


    @Override
    public List<ProductResponse> getAllProducts(String keyword, Long categoryId,
           Long originId, Long brandId, Long styleId, Long materialId, PageRequest pageRequest) throws JsonProcessingException{
        if (!useRedisCache) {
            return null;
        }
        String key = this.getKeyFrom(keyword, categoryId, originId, brandId, styleId, materialId, pageRequest);
        String json = (String) redisTemplate.opsForValue().get(key);
        List<ProductResponse> productResponses =
        json != null ? redisObjectMapper.readValue(json, new TypeReference<List<ProductResponse>>() {}): null;
        return productResponses;
    }

    @Override
    public void saveAllProducts(List<ProductResponse> productResponses, String keyword, Long categoryId,
        Long originId, Long brandId, Long styleId, Long materialId, PageRequest pageRequest) throws JsonProcessingException{
        String key = this.getKeyFrom(keyword, categoryId, originId, brandId, styleId, materialId, pageRequest);
        String json = redisObjectMapper.writeValueAsString(productResponses);
        redisTemplate.opsForValue().set(key, json);
    }
}
