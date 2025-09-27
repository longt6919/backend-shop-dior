package com.project.shop_dior.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shop_dior.models.OrderDetail;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailResponse {
    private Long id;

    @JsonProperty("order_id")
    private Long orderId;

    @JsonProperty("product_detail_id")
    private Long productDetailId;

    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("thumbnail")
    private String thumbnail;

    @JsonProperty("size")
    private String size;

    @JsonProperty("color")
    private String color;

    @JsonProperty("price")
    private BigDecimal price;

    @JsonProperty("number_of_products")
    private int numberOfProducts;

//    @JsonProperty("total_money")
//    private BigDecimal totalMoney;


    public static OrderDetailResponse fromOrderDetail(OrderDetail orderDetail){
        OrderDetailResponse productResponse = OrderDetailResponse
                .builder()
                .id(orderDetail.getId())
                .orderId(orderDetail.getOrder().getId())
                .productId(orderDetail.getProductDetail().getProduct().getId())
                .productName(orderDetail.getProductDetail().getProduct().getName())
                .price(orderDetail.getPrice())
                .size(orderDetail.getProductDetail().getSize().getName())
                .color(orderDetail.getProductDetail().getColor().getName())
                .numberOfProducts(orderDetail.getNumberOfProducts())
//              .totalMoney(orderDetail.getTotalMoney())
                .build();
        return productResponse;
    }

}
