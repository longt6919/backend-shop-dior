package com.project.shop_dior.service;
import com.project.shop_dior.component.JwtTokenUtil;
import com.project.shop_dior.dtos.CartItemDTO;
import com.project.shop_dior.dtos.OrderDTO;
import com.project.shop_dior.exception.DataNotFoundException;
import com.project.shop_dior.exception.OutOfStockException;
import com.project.shop_dior.models.*;
import com.project.shop_dior.repository.*;
import com.project.shop_dior.responses.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ModelMapper modelMapper;
    private final CouponRepository couponRepository;
    private final ProductDetailRepository productDetailRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final OrderStateService orderStateService;

    @Override
    @Transactional
    public Order createOrder(OrderDTO orderDTO) throws DataNotFoundException {
        User user = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(()->new DataNotFoundException("Cannot find user with id: "+orderDTO.getUserId()));
        //converse orderDTO =>Order
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper ->mapper.skip(Order::setId));
        Order order = new Order();
        modelMapper.map(orderDTO,order);
        order.setUser(user);
        order.setOrderDate(new Date());// lay thoi gian hien tai
        order.setStatus(OrderStatus.PENDING);
        //Kiểm tra shipping date phải >= ngày hôm nay
//        LocalDate shippingDate = orderDTO.getShippingDate() == null
//                ? LocalDate.now():orderDTO.getShippingDate();
//        if (shippingDate.isBefore( LocalDate.now())){
//            throw new DataNotFoundException("Date must be at least today !");
//        }
//        order.setShippingDate(shippingDate);
        order.setActive(true);
        order.setTotalMoney(orderDTO.getTotalMoney());
        if(orderDTO.getShippingAddress() == null) {
            order.setShippingAddress(orderDTO.getAddress());
        }
        // tao danh sach cac doi tuong tu OrderDetail tu cartItems
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (CartItemDTO cartItemDTO : orderDTO.getCartItem()) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);

            // Lấy thông tin từ cartItemDTO
            Long productId = cartItemDTO.getProductId();
            int quantity = cartItemDTO.getQuantity();
            Long sizeId = cartItemDTO.getSizeId();
            Long colorId = cartItemDTO.getColorId();

            // Lookup product_detail_id từ 3 trường này:
            ProductDetail productDetail = productDetailRepository
                    .findByProductIdAndSizeIdAndColorId(productId, sizeId, colorId)
                    .orElseThrow(() -> new DataNotFoundException(
                            String.format("Không tìm thấy variant product=%d, size=%d, color=%d",
                                    productId, sizeId, colorId)
                    ));

            // Đặt thông tin cho OrderDetail
            orderDetail.setProductDetail(productDetail);
            orderDetail.setNumberOfProducts(quantity);
            orderDetail.setPrice(productDetail.getProduct().getPrice());

            orderDetails.add(orderDetail);
        }

        //coupon
        Coupon coupon = null;
        String couponCode = orderDTO.getCouponCode();
        if (!couponCode.isEmpty()) {
            coupon = couponRepository.findByCode(couponCode)
                    .orElseThrow(() -> new IllegalArgumentException("Voucher không khả dụng"));
            order.setCoupon(coupon);
        } else {
            order.setCoupon(null);
        }


        orderRepository.save(order);
        BigDecimal discountPercent = BigDecimal.ZERO;
        if (coupon != null) {
            List<CouponCondition> conds = coupon.getConditions();
            if (!conds.isEmpty()) {
                BigDecimal raw = conds.get(0).getDiscountAmount();      // ví dụ 10
                discountPercent = raw.divide(BigDecimal.valueOf(100)); // → 0.10
            }
        }

        // *** THÊM ***: Tính totalMoney và gán coupon cho từng detail
        for (OrderDetail od : orderDetails) {
            BigDecimal subTotal = od.getPrice()
                    .multiply(BigDecimal.valueOf(od.getNumberOfProducts()));
            BigDecimal discountValue = subTotal.multiply(discountPercent);
            od.setTotalMoney(subTotal.subtract(discountValue));
            if (coupon != null) {
                od.setCoupon(coupon);
            }
        }
        orderDetailRepository.saveAll(orderDetails);
        orderRepository.save(order);
        return order;
    }

    @Override
    @Transactional
    public Order createOrderPos(OrderDTO orderDTO) throws DataNotFoundException {
        User user = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(()->new DataNotFoundException("Cannot find user with id: "+orderDTO.getUserId()));

        //converse orderDTO =>Order
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper ->mapper.skip(Order::setId));
        Order order = new Order();
//        ModelMapper giúp map chuyển dữ liệu giữa 2 class với nhau vd như dto->entity
        modelMapper.map(orderDTO,order);
        order.setUser(user);
        order.setOrderDate(new Date());// lay thoi gian hien tai
        order.setStatus(OrderStatus.DELIVERED);
        //Kiểm tra shipping date phải >= ngày hôm nay
//        LocalDate shippingDate = orderDTO.getShippingDate() == null
//                ? LocalDate.now():orderDTO.getShippingDate();
//        if (shippingDate.isBefore( LocalDate.now())){
//            throw new DataNotFoundException("Ngày gửi hàng phải là hôm nay !");
//        }
//        order.setShippingDate(shippingDate);
// .      Kiểm tra delivery date phải >= ngày hôm nay
//        LocalDate deliveryDate = orderDTO.getDeliveryDate() == null
//                ? LocalDate.now():orderDTO.getDeliveryDate();
//        if (deliveryDate.isBefore( LocalDate.now())){
//            throw new DataNotFoundException("Thanh toán thành công phải là hôm nay !");
//        }
//        order.setDeliveryDate(deliveryDate);
        order.setDeliveryDate(new Date());// lay thoi gian hien tai
        order.setActive(true);
        order.setTotalMoney(orderDTO.getTotalMoney());
        if(orderDTO.getShippingAddress() == null) {
            order.setShippingAddress(orderDTO.getAddress());
        }
        // tao danh sach cac doi tuong tu OrderDetail tu cartItems
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (CartItemDTO cartItemDTO : orderDTO.getCartItem()) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);

            // Lấy thông tin từ cartItemDTO
            Long productId = cartItemDTO.getProductId();
            int quantity = cartItemDTO.getQuantity();
            Long sizeId = cartItemDTO.getSizeId();
            Long colorId = cartItemDTO.getColorId();

            // Lookup product_detail_id từ 3 trường này:
            ProductDetail productDetail = productDetailRepository
                    .findByProductIdAndSizeIdAndColorId(productId, sizeId, colorId)
                    .orElseThrow(() -> new DataNotFoundException(
                            String.format("Không tìm thấy variant product=%d, size=%d, color=%d",
                                    productId, sizeId, colorId)
                    ));
            int affected = productDetailRepository.updateQuantity(productDetail.getId(), quantity);
            if (affected == 0) {
                throw new DataNotFoundException("Thiếu hàng cho biến thể id = "  + productDetail.getId());
            }
            // Đặt thông tin cho OrderDetail
            orderDetail.setProductDetail(productDetail);
            orderDetail.setNumberOfProducts(quantity);
            orderDetail.setPrice(productDetail.getProduct().getPrice());

            orderDetails.add(orderDetail);
        }

        //coupon
        Coupon coupon = null;
        String couponCode = orderDTO.getCouponCode();
        if (couponCode != null && !couponCode.isBlank()) {
            // Chỉ lấy coupon đang bật (active = 1)
            coupon = couponRepository.findByCodeAndActiveTrue(couponCode.trim())
                    .orElseThrow(() -> new IllegalArgumentException("Voucher không khả dụng hoặc đã tắt"));
            order.setCoupon(coupon);
        } else {
            order.setCoupon(null);
        }


        orderRepository.save(order);
        BigDecimal discountPercent = BigDecimal.ZERO;
        if (coupon != null) {
            List<CouponCondition> conds = coupon.getConditions();
            if (!conds.isEmpty()) {
                BigDecimal raw = conds.get(0).getDiscountAmount();      // ví dụ 10
                discountPercent = raw.divide(BigDecimal.valueOf(100)); // → 0.10
            }
        }

        // *** THÊM ***: Tính totalMoney và gán coupon cho từng detail
        for (OrderDetail od : orderDetails) {
            BigDecimal subTotal = od.getPrice()
                    .multiply(BigDecimal.valueOf(od.getNumberOfProducts()));
            BigDecimal discountValue = subTotal.multiply(discountPercent);
            od.setTotalMoney(subTotal.subtract(discountValue));
            if (coupon != null) {
                od.setCoupon(coupon);
            }
        }
        orderDetailRepository.saveAll(orderDetails);
        orderRepository.save(order);
        return order;
    }


    @Override
    public Order getOrder(Long id) {

        return orderRepository.findById(id).orElse(null);
    }

    @Override
    public Order getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        return order;
    }

    @Override
    public Order updateOrder(Long id, OrderDTO orderDTO) throws DataNotFoundException {
        Order order = orderRepository.findById(id).orElseThrow(()->
                new DataNotFoundException("Cannot find order with id: "+id));
        User existingUser = userRepository.findById(orderDTO.getUserId()).orElseThrow(()->
                new DataNotFoundException("Cannot find user with id: "+id));
        modelMapper.typeMap(OrderDTO.class,Order.class)
                .addMappings(mapper->mapper.skip(Order::setId));
        modelMapper.map(orderDTO,order);
        order.setUser(existingUser);
        return orderRepository.save(order);
    }

    @Override
    public List<Order> findOrderUserFromToken(String token) throws Exception
    {
        if (jwtTokenUtil.isTokenExpired(token)){ //Sử dụng tiện ích jwtTokenUtil để kiểm tra xem token có hết hạn không
            throw new Exception("Token is expired");
        }
        Long userId = jwtTokenUtil.getUserIdFromToken(token);

        if (userId == null) {
            throw new Exception("User ID not found in token");
        }
        List<Order> orders = orderRepository.findByUserId(userId);

        return orders;
    }

    @Override
    public void deleteOrder(Long id) throws DataNotFoundException {
        Order order = orderRepository.findById(id).orElse(null);
        if(order!=null){
            order.setActive(false);
            orderRepository.save(order);
        }
    }

    @Override
    public Page<Order> getOrdersByKeyword(String keyword, Pageable pageable) {
                 return orderRepository.findByKeyword(keyword, pageable);
    }

    @Override
    public Page<Order> getBillsByKeyword(String keyword, Pageable pageable) {
        return orderRepository.findDeliveredOrders(keyword, pageable);
    }

    @Override
    public Page<Order> getBillsToPosByKeyword(String keyword, Pageable pageable) {
        return orderRepository.findDeliveredOrdersAtCounter(keyword,pageable);
    }

    @Override
    public Page<Order> getBillsToOnlineByKeyword(String keyword, Pageable pageable) {
        return orderRepository.findDeliveredOrdersNotCounter(keyword,pageable);
    }


    @Override
    @Transactional
    public Order updateOrderStatus(Long id, String status) throws DataNotFoundException, OutOfStockException {
        Order order = getOrderById(id);

        if (status == null || status.trim().isEmpty())
            throw new IllegalArgumentException("Status cannot be null or empty");
        if (!OrderStatus.VALID_STATUSES.contains(status))
            throw new IllegalArgumentException("Invalid status: " + status);

        String currentStatus = order.getStatus();

        // nếu ko thay đổi gì thì giữ nguyên
        if (status.equals(currentStatus)) return order;

        // Không cho đổi từ trạng thái kết thúc
        if (currentStatus.equals(OrderStatus.DELIVERED)
                || currentStatus.equals(OrderStatus.CANCELLED)
                || currentStatus.equals(OrderStatus.CANCELLED_OUT_OF_STOCK)) {
            throw new IllegalArgumentException(
                    "Không thể chuyển từ " + OrderStatus.vi(currentStatus) + " sang " + OrderStatus.vi(status)
            );        }

        // Chặn đi tắt khi đã SHIPPED
        if ((status.equals(OrderStatus.CANCELLED) || status.equals(OrderStatus.CANCELLED_OUT_OF_STOCK))
                && currentStatus.equals(OrderStatus.SHIPPED)) {
            throw new IllegalArgumentException("Không thể hủy khi đơn đã gửi đi");
        }

        // Không cho hủy do hết hàng sau khi khách đã từ chối nhận
        if (status.equals(OrderStatus.CANCELLED_OUT_OF_STOCK)
                && currentStatus.equals(OrderStatus.REFUSED_ON_DELIVERY)) {
            throw new IllegalArgumentException("Không thể hủy do hết hàng sau khi khách từ chối nhận.");
        }

        // từ WAITING_FOR_STOCK -> PROCESSING (trừ tồn)
        if (status.equals(OrderStatus.PROCESSING) && currentStatus.equals(OrderStatus.WAITING_FOR_STOCK)) {
            for (OrderDetail od : order.getOrderDetails()) {
                int ok = productDetailRepository.updateQuantity(
                        od.getProductDetail().getId(), od.getNumberOfProducts());
                if (ok == 0) throw new IllegalArgumentException("Hàng chưa đủ để xử lý.");
            }
            order.setStatus(OrderStatus.PROCESSING);
            return orderRepository.save(order);
        }
        // từ OUT_OF_STOCK_PENDING -> PROCESSING (trừ tồn)
        if (status.equals(OrderStatus.PROCESSING) && currentStatus.equals(OrderStatus.OUT_OF_STOCK_PENDING)) {
                for (OrderDetail od : order.getOrderDetails()) {
                    int ok = productDetailRepository.updateQuantity(
                            od.getProductDetail().getId(), od.getNumberOfProducts());
                    if (ok == 0) throw new IllegalArgumentException("Hàng chưa đủ để xử lý.");
                }
                order.setStatus(OrderStatus.PROCESSING);
                return orderRepository.save(order);
        }

        // ===== PENDING -> PROCESSING (trừ tồn) =====
        if (status.equals(OrderStatus.PROCESSING) && currentStatus.equals(OrderStatus.PENDING)) {
            for (OrderDetail od : order.getOrderDetails()) {
                Long detailId = od.getProductDetail().getId();
                int qty = od.getNumberOfProducts();
                int affected = productDetailRepository.updateQuantity(detailId, qty); // UPDATE ... AND quantity >= :qty
                if (affected == 0) {
                    orderStateService.markOutOfStockPending(order.getId(), "Thiếu hàng khi xác nhận.");
                    throw new OutOfStockException("Thiếu hàng");
                }
            }
            order.setStatus(OrderStatus.PROCESSING);
            return orderRepository.save(order);
        }

        //  HỦY
        if (status.equals(OrderStatus.CANCELLED) || status.equals(OrderStatus.CANCELLED_OUT_OF_STOCK)) {
            // Chỉ hoàn kho nếu đã từng trừ tồn
            if (currentStatus.equals(OrderStatus.PROCESSING)) {
                for (OrderDetail od : order.getOrderDetails()) {
                    ProductDetail pd = od.getProductDetail();
                    pd.setQuantity(pd.getQuantity() + od.getNumberOfProducts());
                    productDetailRepository.save(pd);
                }
                //nếu ko thuộc những trạng thái này thì ko thể hủy còn nếu là 1 trong những stt này thì hủy ko + tôn
            } else if (!(currentStatus.equals(OrderStatus.PENDING)
                    || currentStatus.equals(OrderStatus.OUT_OF_STOCK_PENDING)
                    || currentStatus.equals(OrderStatus.WAITING_FOR_STOCK)
                    || currentStatus.equals(OrderStatus.REFUSED_ON_DELIVERY))) {
                throw new IllegalArgumentException("Không thể hủy từ trạng thái: " + currentStatus);
            }
            order.setStatus(status);
            return orderRepository.save(order);
        }

        // ===== PROCESSING -> SHIPPED =====
        if (status.equals(OrderStatus.SHIPPED)) {
            if (!currentStatus.equals(OrderStatus.PROCESSING)) {
                throw new IllegalArgumentException("Chỉ có thể chuyển 'Đang giao hàng' từ 'Đã nhận hàng'");
            }
            order.setShippingDate(LocalDate.now());
            order.setStatus(OrderStatus.SHIPPED);
            return orderRepository.save(order);
        }
        // ===== SHIPPED -> DELIVERED =====
        if (status.equals(OrderStatus.DELIVERED)) {
            if (!currentStatus.equals(OrderStatus.SHIPPED)) {
                throw new IllegalArgumentException("Chỉ có thể chuyển 'Đã nhận hàng' từ 'Đang giao hàng' ");
            }
            order.setDeliveryDate(new Date());
            order.setStatus(OrderStatus.DELIVERED);
            return orderRepository.save(order);
        }

        // ===== SHIPPED -> REFUSED_ON_DELIVERY (khách không nhận): hoàn kho 1 lần =====
        if (status.equals(OrderStatus.REFUSED_ON_DELIVERY) && currentStatus.equals(OrderStatus.SHIPPED)) {
            for (OrderDetail od : order.getOrderDetails()) {
                ProductDetail pd = od.getProductDetail();
                pd.setQuantity(pd.getQuantity() + od.getNumberOfProducts());
                productDetailRepository.save(pd);
            }
            order.setStatus(OrderStatus.REFUSED_ON_DELIVERY);
            return orderRepository.save(order);
        }

        // ===== REFUSED_ON_DELIVERY -> CANCELLED (không + tồn nữa) =====
        if (status.equals(OrderStatus.CANCELLED) && currentStatus.equals(OrderStatus.REFUSED_ON_DELIVERY)) {
            order.setStatus(OrderStatus.CANCELLED);
            return orderRepository.save(order);
        }

        // (tuỳ chọn) OUT_OF_STOCK_PENDING -> WAITING_FOR_STOCK
        if (status.equals(OrderStatus.WAITING_FOR_STOCK) && currentStatus.equals(OrderStatus.OUT_OF_STOCK_PENDING)) {
            order.setStatus(OrderStatus.WAITING_FOR_STOCK);
            return orderRepository.save(order);
        }
        // Không khớp rule nào
        throw new IllegalArgumentException(
                "Không thể chuyển từ " + OrderStatus.vi(currentStatus) + " sang " + OrderStatus.vi(status)
        );    }



    @Override
    public List<Order> findByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }
}
//  Kiểm tra tồn kho khi chuyển sang PROCESSING
//        if (status.equals(OrderStatus.PROCESSING)) {
//            for (OrderDetail detail : order.getOrderDetails()) {
//                ProductDetail productDetail = detail.getProductDetail();
//                int currentQty = productDetail.getQuantity();
//                int orderedQty = detail.getNumberOfProducts();
//                if (currentQty < orderedQty) {
//                    throw new IllegalArgumentException(
//                            "Sản phẩm " + productDetail.getProduct().getName() + " không đủ số lượng tồn kho");
//                }
//            }
//        }
//        if (status.equals(OrderStatus.PROCESSING)) {
//            for (OrderDetail orderDetail : order.getOrderDetails()) {
//                ProductDetail productDetail = orderDetail.getProductDetail();
//                int currentQty = productDetail.getQuantity();
//                int orderedQty = orderDetail.getNumberOfProducts();
//                productDetail.setQuantity(currentQty - orderedQty);
//                productDetailRepository.save(productDetail);
//            }
//        }