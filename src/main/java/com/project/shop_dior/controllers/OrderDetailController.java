package com.project.shop_dior.controllers;

import com.project.shop_dior.dtos.OrderDetailDTO;
import com.project.shop_dior.exception.DataNotFoundException;
import com.project.shop_dior.models.OrderDetail;
import com.project.shop_dior.responses.OrderDetailResponse;
import com.project.shop_dior.service.OrderDetailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/order_detail")
@RequiredArgsConstructor
public class OrderDetailController {
    private final OrderDetailService orderDetailService;
    @PostMapping("")
    public ResponseEntity<?> createOrderDetail(
            @RequestBody @Valid OrderDetailDTO orderDetailDTO){

        try{
            OrderDetail newOrderDetail = orderDetailService.createOrderDetail(orderDetailDTO);
            return ResponseEntity.ok().body(OrderDetailResponse.fromOrderDetail(newOrderDetail));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderDetail(
            @Valid @PathVariable("id") Long id) throws DataNotFoundException {
        OrderDetail orderDetail = orderDetailService.getOrderDetail(id);
        return ResponseEntity.ok().body(OrderDetailResponse.fromOrderDetail(orderDetail));
    }
    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getOrderDetails(
            @Valid @PathVariable("orderId") Long orderId){
        List<OrderDetail> orderDetails = orderDetailService.findByOrderId(orderId);
        List<OrderDetailResponse> orderDetailResponses = orderDetails.stream().map(orderDetail ->
                OrderDetailResponse.fromOrderDetail(orderDetail)).toList();
        orderDetailService.findByOrderId(orderId);
        return ResponseEntity.ok(orderDetailResponses);
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<?> updateOrderDetail(
            @Valid @PathVariable("id") Long id,
            @RequestBody OrderDetailDTO orderDetailDTO)  {
        try{
            OrderDetail orderDetail = orderDetailService.updateOrderDetail(id,orderDetailDTO);
            return ResponseEntity.ok(orderDetail);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<?> deleteDetail(
            @Valid @PathVariable("id") Long id) throws DataNotFoundException {
        orderDetailService.deleteOrderDetail(id);
        return ResponseEntity.ok().body("Xoá thành công order detail id: "+id);
    }
}
