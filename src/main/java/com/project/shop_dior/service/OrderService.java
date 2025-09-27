package com.project.shop_dior.service;

import com.project.shop_dior.dtos.OrderDTO;
import com.project.shop_dior.exception.DataNotFoundException;
import com.project.shop_dior.exception.OutOfStockException;
import com.project.shop_dior.models.Order;
import com.project.shop_dior.responses.OrderListResponse;
import com.project.shop_dior.responses.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    Order createOrder(OrderDTO orderDTO) throws DataNotFoundException;
    Order createOrderPos(OrderDTO orderDTO) throws DataNotFoundException;
    Order getOrder(Long id);
    Order getOrderById(Long orderId);
    Order updateOrder(Long id, OrderDTO orderDTO) throws DataNotFoundException;
    List<Order> findOrderUserFromToken(String token) throws Exception;
    void deleteOrder(Long id) throws DataNotFoundException;
    Page<Order> getOrdersByKeyword(String keyword, Pageable pageable);
    Page<Order> getBillsByKeyword(String keyword, Pageable pageable);
    Page<Order> getBillsToPosByKeyword(String keyword, Pageable pageable);
    Page<Order> getBillsToOnlineByKeyword(String keyword, Pageable pageable);
    Order updateOrderStatus(Long id, String status) throws DataNotFoundException, OutOfStockException;
    List<Order> findByUserId(Long userId);
}
