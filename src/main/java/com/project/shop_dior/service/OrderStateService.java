package com.project.shop_dior.service;

import com.project.shop_dior.models.Order;
import com.project.shop_dior.models.OrderStatus;
import com.project.shop_dior.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderStateService {
@Autowired OrderRepository orderRepository;
@Transactional(propagation = Propagation.REQUIRES_NEW)
public void markOutOfStockPending(Long orderId, String note) {
    Order o = orderRepository.findById(orderId).orElseThrow();
    o.setStatus(OrderStatus.OUT_OF_STOCK_PENDING);
    o.setNote(("Thiếu hàng khi xác nhận. CSKH sẽ liên hệ bạn."));
    orderRepository.save(o); // COMMIT trong TX mới
}
}
