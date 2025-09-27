package com.project.shop_dior.service;

import com.project.shop_dior.dtos.PaymentDTO;
import com.project.shop_dior.dtos.PaymentQueryDTO;
import com.project.shop_dior.dtos.PaymentRefundDTO;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

public interface VNPayService {
    String createPaymentUrl(PaymentDTO paymentDTO, HttpServletRequest httpRequest);
    String queryTransaction(PaymentQueryDTO queryDto, HttpServletRequest httpRequest) throws IOException;
    String refundTransaction(PaymentRefundDTO refundRequest) throws IOException;
}
