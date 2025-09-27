package com.project.shop_dior.service;

import com.project.shop_dior.dtos.OrderDetailDTO;
import com.project.shop_dior.exception.DataNotFoundException;
import com.project.shop_dior.models.OrderDetail;

import java.util.List;

public interface OrderDetailService {
    OrderDetail createOrderDetail(OrderDetailDTO orderDTO) throws Exception;
    OrderDetail getOrderDetail(Long id) throws DataNotFoundException;
    OrderDetail updateOrderDetail(Long id, OrderDetailDTO orderDTO) throws Exception;
    List<OrderDetail> findByOrderId(Long orderId);
    void deleteOrderDetail(Long id) throws DataNotFoundException;
}
