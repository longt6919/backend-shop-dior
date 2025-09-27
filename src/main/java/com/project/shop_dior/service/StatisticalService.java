package com.project.shop_dior.service;


import com.project.shop_dior.responses.DeliveryRevenueResponse;
import com.project.shop_dior.responses.RoleCountResponse;

import java.util.List;

public interface StatisticalService {
    DeliveryRevenueResponse getDeliveryRevenueByMonth(int year, int month);
    List<DeliveryRevenueResponse> getDeliveryRevenueForYear(int year);
    List<RoleCountResponse> getUserRoleCounts();
}