package com.project.shop_dior.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
public class OrderDTO {
    @JsonProperty("user_id")
    @Min(value = 1,message = "User's ID must be >0")
    private Long userId;
    @JsonProperty("fullname")
    private String fullName;
    private String email;
    @JsonProperty("phone_number")
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
    @JsonProperty("status")
    private String status;
    private String address;
    private String note;
    @JsonProperty("total_money")
    @Min(value = 0,message = "Total money must be >=0")
    private BigDecimal totalMoney;
    @JsonProperty("shipping_method")
    private String shippingMethod;
    @JsonProperty("shipping_address")
    private String shippingAddress;
    @JsonProperty("payment_method")
    private String paymentMethod;
    @JsonProperty("shipping_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate shippingDate;
    @JsonProperty("delivery_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Date deliveryDate;
    @JsonProperty("cart_items")
    private List<CartItemDTO> cartItem = new ArrayList<>();
    @JsonProperty("coupon_code")
    private String couponCode;

}
