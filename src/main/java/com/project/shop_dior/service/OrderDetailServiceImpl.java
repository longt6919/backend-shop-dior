package com.project.shop_dior.service;

import com.project.shop_dior.dtos.OrderDetailDTO;
import com.project.shop_dior.exception.DataNotFoundException;
import com.project.shop_dior.models.Order;
import com.project.shop_dior.models.OrderDetail;
import com.project.shop_dior.models.Product;
import com.project.shop_dior.models.ProductDetail;
import com.project.shop_dior.repository.OrderDetailRepository;
import com.project.shop_dior.repository.OrderRepository;
import com.project.shop_dior.repository.ProductDetailRepository;
import com.project.shop_dior.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderDetailServiceImpl  implements OrderDetailService{
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;
    private final ProductDetailRepository productDetailRepository;

    @Override
    @Transactional
    public OrderDetail createOrderDetail(OrderDetailDTO orderDetailDTO) throws Exception {
        Order order = orderRepository.findById(orderDetailDTO.getOrderId())
                .orElseThrow(()->new DataNotFoundException("Cannot find order with id: "+orderDetailDTO.getOrderId()));
        //tim product theo id
        ProductDetail productDetail = productDetailRepository.findById(orderDetailDTO.getProductDetailId())
                .orElseThrow(()->new DataNotFoundException("Cannot find product detail with id: "+orderDetailDTO.getProductDetailId()));

        // 3. Kiểm tra tồn kho
        if (productDetail.getQuantity() < orderDetailDTO.getNumberOfProducts()) {
            throw new IllegalArgumentException("Không đủ biến thể tồn có id: "
                    + orderDetailDTO.getProductDetailId());
        }

        BigDecimal totalMoney = orderDetailDTO.getPrice().multiply(BigDecimal.valueOf(orderDetailDTO.getNumberOfProducts()));
        OrderDetail orderDetail = OrderDetail.builder()
                .order(order)
                .productDetail(productDetail)
                .price(orderDetailDTO.getPrice())
                .numberOfProducts(orderDetailDTO.getNumberOfProducts())
                .totalMoney(totalMoney)
                .build();
        return orderDetailRepository.save(orderDetail);
    }

    @Override
    public OrderDetail getOrderDetail(Long id) throws DataNotFoundException {
        return orderDetailRepository.findById(id).orElseThrow(()->new DataNotFoundException("Cannot find OrderDetail with id: "+id));
    }

    @Override
    @Transactional
    public OrderDetail updateOrderDetail(Long id, OrderDetailDTO orderDetailDTO) throws Exception {
        //tim xem order detail co ton tai hay khong
        OrderDetail existingOrderDetail = orderDetailRepository.findById(id)
                .orElseThrow(()->new DataNotFoundException("Cannot find order detail with id: "+id));

        Order existingOrder = orderRepository.findById(orderDetailDTO.getOrderId())
                .orElseThrow(()-> new DataNotFoundException("Cannot find order with id: "+id));

        ProductDetail existingProductDetail = productDetailRepository.findById(orderDetailDTO.getProductDetailId())
                .orElseThrow(()->new DataNotFoundException("Cannot find order detail with id: " +id));

        existingOrderDetail.setPrice(orderDetailDTO.getPrice());
        existingOrderDetail.setTotalMoney(orderDetailDTO.getTotalMoney());
//        BigDecimal total = orderDetailDTO.getPrice()
//                .multiply(BigDecimal.valueOf(orderDetailDTO.getNumberOfProducts()));
//        existingOrderDetail.setTotalMoney(total);
        existingOrderDetail.setNumberOfProducts(orderDetailDTO.getNumberOfProducts());
        existingOrderDetail.setOrder(existingOrder);
        existingOrderDetail.setProductDetail(existingProductDetail);
        return orderDetailRepository.save(existingOrderDetail);
    }

    @Override
    public List<OrderDetail> findByOrderId(Long orderId) {
        return orderDetailRepository.findByOrderId(orderId);
    }

    @Override
    @Transactional
    public void deleteOrderDetail(Long id) throws DataNotFoundException {
        orderDetailRepository.deleteById(id);
    }
}
