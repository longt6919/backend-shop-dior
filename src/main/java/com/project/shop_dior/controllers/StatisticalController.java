package com.project.shop_dior.controllers;

import com.project.shop_dior.responses.DeliveryRevenueResponse;
import com.project.shop_dior.responses.RoleCountResponse;
import com.project.shop_dior.service.StatisticalService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/statistical")
@RequiredArgsConstructor
public class StatisticalController {
    private final StatisticalService statisticalService;
    @GetMapping("")
    @PreAuthorize("hasAnyAuthority('admin')")
    public DeliveryRevenueResponse deliveryRevenue(
            @RequestParam int year,
            @RequestParam int month
    ) {
        return statisticalService.getDeliveryRevenueByMonth(year, month);
    }
    @GetMapping("/revenue-by-month")
    @PreAuthorize("hasAnyAuthority('admin')")
    public List<DeliveryRevenueResponse> revenueByMonth(@RequestParam int year) {
        return statisticalService.getDeliveryRevenueForYear(year);
    }

    @GetMapping("/user-role-counts")
    @PreAuthorize("hasAnyAuthority('admin')")
    public List<RoleCountResponse> userRoleCounts() {
        return statisticalService.getUserRoleCounts();
    }
}
