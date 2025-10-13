package com.project.shop_dior.listeners;

/**
 * Sự kiện được phát ra khi sản phẩm bị thay đổi
 * (tạo mới, cập nhật, hoặc xóa).
 * Listener như ProductCacheInvalidator sẽ lắng nghe sự kiện này.
 */
public record ProductChangedEvent(Long productId) {}
