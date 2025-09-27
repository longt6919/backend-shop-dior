package com.project.shop_dior.service;

import com.project.shop_dior.repository.OrderRepository;
import com.project.shop_dior.repository.UserRepository;
import com.project.shop_dior.responses.DeliveryRevenueResponse;
import com.project.shop_dior.responses.RoleCountResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticalServiceImpl implements StatisticalService{
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Override
    public DeliveryRevenueResponse getDeliveryRevenueByMonth(int year, int month) {
        //Tạo ra một LocalDate ứng với ngày đầu tiên của tháng
        LocalDate first = LocalDate.of(year, month, 1);
        //Chuyển LocalDate thành LocalDateTime tại 00:00:00 (đầu ngày) để bắt đầu query
        LocalDateTime start = first.atStartOfDay();
        LocalDateTime end = first.plusMonths(1).atStartOfDay();
        BigDecimal total = orderRepository.sumDeliveredBetween(start, end);
        return new DeliveryRevenueResponse(year, month, total);
    }

    @Override
    public List<DeliveryRevenueResponse> getDeliveryRevenueForYear(int year) {
        List<DeliveryRevenueResponse> result = new ArrayList<>(12);
        for (int month = 1; month <= 12; month++) {
            LocalDate first = LocalDate.of(year, month, 1);
            LocalDateTime start = first.atStartOfDay();
            LocalDateTime end   = first.plusMonths(1).atStartOfDay();
            BigDecimal total = orderRepository.sumDeliveredBetween(start, end);
            if (total == null) total = BigDecimal.ZERO;
            result.add(new DeliveryRevenueResponse(year, month, total));
        }
        return result;
    }

    @Override
    public List<RoleCountResponse> getUserRoleCounts() {
        return userRepository.countUsersByRoleNames(List.of("user", "employee"))
                .stream()
                .map(r -> new RoleCountResponse(
                        (String) r[0],
                        ((Number) r[1]).longValue()
                ))
                .toList();
    }
}
