package com.medical.management.payments.service;

import com.medical.management.common.exception.BadRequestException;
import com.medical.management.common.exception.ResourceNotFoundException;
import com.medical.management.common.response.PageResponse;
import com.medical.management.payments.dto.PaymentConfirmRequest;
import com.medical.management.payments.dto.PaymentCreateRequest;
import com.medical.management.payments.dto.PaymentResponse;
import com.medical.management.payments.entity.Payment;
import com.medical.management.payments.entity.PaymentStatus;
import com.medical.management.payments.repository.PaymentRepository;
import com.medical.management.treatmentstages.entity.TreatmentStage;
import com.medical.management.treatmentstages.repository.TreatmentStageRepository;
import com.medical.management.users.entity.User;
import com.medical.management.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final TreatmentStageRepository treatmentStageRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PaymentResponse> getPayments(Long userId, PaymentStatus status, Pageable pageable) {
        Page<Payment> page = paymentRepository.searchPayments(userId, status, pageable);
        return PageResponse.from(page.map(PaymentResponse::fromEntity));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PaymentResponse> getPaymentsByUserId(Long userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        Page<Payment> page = paymentRepository.findByUserId(userId, pageable);
        return PageResponse.from(page.map(PaymentResponse::fromEntity));
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long id) {
        Payment payment = paymentRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));
        return PaymentResponse.fromEntity(payment);
    }

    @Override
    @Transactional
    public PaymentResponse createPayment(PaymentCreateRequest request) {
        User user = userRepository.findByIdWithRole(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));

        TreatmentStage treatmentStage = null;
        if (request.getTreatmentStageId() != null) {
            treatmentStage = treatmentStageRepository.findByIdWithRoute(request.getTreatmentStageId())
                    .orElseThrow(() -> new ResourceNotFoundException("TreatmentStage", "id", request.getTreatmentStageId()));
        }

        // Always initiate as PENDING; client cannot decide SUCCESS directly!
        Payment payment = Payment.builder()
                .user(user)
                .amount(request.getAmount())
                .method(request.getMethod())
                .status(PaymentStatus.PENDING)
                .treatmentStage(treatmentStage)
                .build();

        Payment saved = paymentRepository.save(payment);
        log.info("Payment initiated with ID: {}, amount: {}, method: {}", saved.getId(), saved.getAmount(), saved.getMethod());
        return PaymentResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public PaymentResponse confirmPayment(Long id, PaymentConfirmRequest request) {
        Payment payment = paymentRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            throw new BadRequestException("Payment is already confirmed as SUCCESS");
        }

        if (request.getTransactionRef() == null || request.getTransactionRef().isBlank()) {
            throw new BadRequestException("Valid transaction reference or verification signature is required");
        }

        payment.setStatus(request.getStatus());
        Payment updated = paymentRepository.save(payment);
        log.info("Payment ID: {} status updated to: {} with ref: {}", id, request.getStatus(), request.getTransactionRef());

        return PaymentResponse.fromEntity(updated);
    }
}
