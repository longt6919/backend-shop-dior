package com.project.shop_dior.component;

import com.project.shop_dior.controllers.ProductController;
import com.project.shop_dior.listeners.ProductChangedEvent;
import com.project.shop_dior.service.ProductRedisService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ProductCacheInvalidator {
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    private final ProductRedisService productRedisService;
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onProductChanged(ProductChangedEvent event) {
        logger.info("[LISTENER] ProductChangedEvent id={}", event.productId());
        productRedisService.clearCacheProduct();
        productRedisService.clearCacheProductActive();
    }
}
