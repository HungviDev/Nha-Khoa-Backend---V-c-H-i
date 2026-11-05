package com.medical.management.payments.service;

import com.medical.management.common.response.PageResponse;
import com.medical.management.payments.dto.PaymentConfirmRequest;
import com.medical.management.payments.dto.PaymentCreateRequest;
import com.medical.management.payments.dto.PaymentResponse;
import com.medical.management.payments.entity.PaymentStatus;
import org.springframework.data.domain.Pageable;

public interface PaymentService {

    PageResponse<PaymentResponse> getPayments(Long userId, PaymentStatus status, Pageable pageable);

    PageResponse<PaymentResponse> getPaymentsByUserId(Long userId, Pageable pageable);

    PaymentResponse getPaymentById(Long id);

    PaymentResponse createPayment(PaymentCreateRequest request);

    PaymentResponse confirmPayment(Long id, PaymentConfirmRequest request);
}
