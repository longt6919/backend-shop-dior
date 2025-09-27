package com.project.shop_dior.models;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class OrderStatus {
    public static final String PENDING ="pending";
    public static final String PROCESSING ="processing";
    public static final String SHIPPED ="shipped";
    public static final String REFUSED_ON_DELIVERY = "refused_on_delivery";
    public static final String DELIVERED ="delivered";
    public static final String CANCELLED ="cancelled";
    public static final String OUT_OF_STOCK_PENDING = "out_of_stock_pending";   // hết hàng, chờ CSKH
    public static final String WAITING_FOR_STOCK    = "waiting_for_stock";      // khách đồng ý chờ
    public static final String CANCELLED_OUT_OF_STOCK = "cancelled_out_of_stock";
    public static final Set<String> VALID_STATUSES = Set.of(
            PENDING, PROCESSING, SHIPPED, REFUSED_ON_DELIVERY, DELIVERED, CANCELLED, OUT_OF_STOCK_PENDING,
            WAITING_FOR_STOCK, CANCELLED_OUT_OF_STOCK
    );
    // === Thêm phần này ===
    private static final Map<String, String> VI_LABELS = Map.of(
            PENDING, "đang chờ",
            PROCESSING, "đã xác nhận",
            SHIPPED, "đang giao hàng",
            REFUSED_ON_DELIVERY, "khách không nhận hàng",
            DELIVERED, "đã nhận hàng",
            CANCELLED, "đã huỷ",
            OUT_OF_STOCK_PENDING, "tạm hết hàng",
            WAITING_FOR_STOCK, "chờ hàng về",
            CANCELLED_OUT_OF_STOCK, "huỷ do hết hàng"
    );

    public static String vi(String status) {
        if (status == null) return "không xác định";
        String key = status.toLowerCase(Locale.ROOT);
        String label = VI_LABELS.get(key);
        return (label != null) ? label : status.replace('_', ' ');
    }

}
