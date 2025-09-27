package com.project.shop_dior.controllers;

import com.project.shop_dior.component.JwtTokenUtil;
import com.project.shop_dior.dtos.OrderDTO;
import com.project.shop_dior.exception.DataNotFoundException;
import com.project.shop_dior.models.Order;
import com.project.shop_dior.responses.OrderListResponse;
import com.project.shop_dior.responses.OrderResponse;
import com.project.shop_dior.responses.ResponseObject;
import com.project.shop_dior.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final JwtTokenUtil jwtTokenUtil;

    @PostMapping("")
    public ResponseEntity<?> createOrder(@RequestBody @Valid OrderDTO orderDTO,
                                         BindingResult result){
        try {
            if(result.hasErrors()){
                List<String> errorMessages = result.getFieldErrors()
                        .stream().map(FieldError::getDefaultMessage).toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            Order order= orderService.createOrder(orderDTO);
            return ResponseEntity.ok(order);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/employee")
    public ResponseEntity<?> createOrderPos(@RequestBody @Valid OrderDTO orderDTO,
                                         BindingResult result){
        try {
            if(result.hasErrors()){
                List<String> errorMessages = result.getFieldErrors()
                        .stream().map(FieldError::getDefaultMessage).toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            Order order= orderService.createOrderPos(orderDTO);
            return ResponseEntity.ok(order);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/find-order-by-user")
    public ResponseEntity<OrderListResponse> getAllOrdersByUserId(@RequestHeader("Authorization") String authorizationHeader){
        try {
            String extractedToken = authorizationHeader.substring(7); // Bỏ "Bearer "
            Long userId = jwtTokenUtil.getUserIdFromToken(extractedToken);
            // Tìm danh sách order theo userId
            List<Order> orders = orderService.findByUserId(userId);
            List<OrderResponse> orderResponses = orders.stream()
                    .map(OrderResponse::fromOrder)
                    .toList();
            OrderListResponse resp = OrderListResponse.builder()
                    .orders(orderResponses)
                    .totalPages(1) // Nếu không phân trang thì để 1
                    .build();

            return ResponseEntity.ok(resp);
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrder(@Valid@PathVariable("id") Long orderId){
        try {
            Order existingOrder = orderService.getOrder(orderId);
            OrderResponse orderResponse = OrderResponse.fromOrder(existingOrder);
            return ResponseEntity.ok(orderResponse);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<?>updateOrder( @PathVariable long id,
                                         @Valid @RequestBody OrderDTO orderDTO){
        try {
            Order order = orderService.updateOrder(id,orderDTO);
            return ResponseEntity.ok(order);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@Valid@PathVariable Long id) throws DataNotFoundException {
        //xoa va cap nhat trong active = false;
        orderService.deleteOrder(id);
        return ResponseEntity.ok("Xóa đơn hàng thành công");
    }
    @GetMapping("/get-orders-by-keyword")
    @PreAuthorize("hasAnyAuthority('admin', 'employee')")
    public ResponseEntity<OrderListResponse> getOrderByKeyword(
            @RequestParam(defaultValue = "", required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int limit
    ){
        System.out.println(SecurityContextHolder.getContext()
                .getAuthentication().getAuthorities());

        // Tạo Pageable từ thông tin trang và giới hạn
        PageRequest pageRequest = PageRequest.of(
                page,limit, Sort.by("id").ascending());

        Page<OrderResponse> orderPage = orderService.getOrdersByKeyword(keyword,pageRequest).map(OrderResponse::fromOrder);
        int totalPages = orderPage.getTotalPages();
        List<OrderResponse> orderResponses = orderPage.getContent();
        return ResponseEntity.ok(OrderListResponse.builder().orders(orderResponses).totalPages(totalPages).build());
    }
    @GetMapping("/get-bills-by-keyword")
    @PreAuthorize("hasAnyAuthority('admin')")
    public ResponseEntity<OrderListResponse> getBillsByKeyword(
            @RequestParam(defaultValue = "", required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int limit
    ){
        System.out.println(SecurityContextHolder.getContext()
                .getAuthentication().getAuthorities());
        // Tạo Pageable từ thông tin trang và giới hạn
        PageRequest pageRequest = PageRequest.of(
                page,limit, Sort.by("id").ascending());

        Page<OrderResponse> orderPage = orderService.getBillsByKeyword(keyword,pageRequest).map(OrderResponse::fromOrder);
        int totalPages = orderPage.getTotalPages();
        List<OrderResponse> orderResponses = orderPage.getContent();
        return ResponseEntity.ok(OrderListResponse.builder().orders(orderResponses).totalPages(totalPages).build());
    }
    @GetMapping("/counter/get-bills-by-keyword")
    @PreAuthorize("hasAnyAuthority('employee','admin')")
    public ResponseEntity<OrderListResponse> getBillsToPosByKeyword(
            @RequestParam(defaultValue = "", required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int limit
    ){
        System.out.println(SecurityContextHolder.getContext()
                .getAuthentication().getAuthorities());
        // Tạo Pageable từ thông tin trang và giới hạn
        PageRequest pageRequest = PageRequest.of(
                page,limit, Sort.by("id").ascending());

        Page<OrderResponse> orderPage = orderService.getBillsToPosByKeyword(keyword,pageRequest).map(OrderResponse::fromOrder);
        int totalPages = orderPage.getTotalPages();
        List<OrderResponse> orderResponses = orderPage.getContent();
        return ResponseEntity.ok(OrderListResponse.builder().orders(orderResponses).totalPages(totalPages).build());
    }
    @GetMapping("/online/get-bills-by-keyword")
    @PreAuthorize("hasAnyAuthority('employee','admin')")
    public ResponseEntity<OrderListResponse> getBillsOnlineByKeyword(
            @RequestParam(defaultValue = "", required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int limit
    ){
        System.out.println(SecurityContextHolder.getContext()
                .getAuthentication().getAuthorities());
        // Tạo Pageable từ thông tin trang và giới hạn
        PageRequest pageRequest = PageRequest.of(
                page,limit, Sort.by("id").ascending());

        Page<OrderResponse> orderPage = orderService.getBillsToOnlineByKeyword(keyword,pageRequest).map(OrderResponse::fromOrder);
        int totalPages = orderPage.getTotalPages();
        List<OrderResponse> orderResponses = orderPage.getContent();
        return ResponseEntity.ok(OrderListResponse.builder().orders(orderResponses).totalPages(totalPages).build());
    }
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('admin','user','employee')")
    public ResponseEntity<ResponseObject> updateOrderStatus(
            @Valid @PathVariable Long id,
            @RequestParam String status) throws Exception {
        // Gọi service để cập nhật trạng thái
        Order updatedOrder = orderService.updateOrderStatus(id, status);
        // Trả về phản hồi thành công
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Order status updated successfully")
                .status(HttpStatus.OK)
                .data(OrderResponse.fromOrder(updatedOrder))
                .build());
    }

}
