package com.medical.management.payments.service;

import com.medical.management.common.exception.BadRequestException;
import com.medical.management.payments.dto.PaymentConfirmRequest;
import com.medical.management.payments.dto.PaymentCreateRequest;
import com.medical.management.payments.dto.PaymentResponse;
import com.medical.management.payments.entity.Payment;
import com.medical.management.payments.entity.PaymentMethod;
import com.medical.management.payments.entity.PaymentStatus;
import com.medical.management.payments.repository.PaymentRepository;
import com.medical.management.treatmentstages.repository.TreatmentStageRepository;
import com.medical.management.users.entity.User;
import com.medical.management.users.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TreatmentStageRepository treatmentStageRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private User patient;

    @BeforeEach
    void setUp() {
        patient = User.builder()
                .id(1L)
                .fullname("Patient B")
                .email("patientb@gmail.com")
                .phone("0912345678")
                .build();
    }

    @Test
    @DisplayName("Should create payment with initial status strictly as PENDING")
    void createPayment_InitialStatusIsPending() {
        PaymentCreateRequest request = PaymentCreateRequest.builder()
                .userId(1L)
                .amount(BigDecimal.valueOf(500000))
                .method(PaymentMethod.VNPAY)
                .build();

        when(userRepository.findByIdWithRole(1L)).thenReturn(Optional.of(patient));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> {
            Payment p = inv.getArgument(0);
            p.setId(100L);
            p.setCreatedAt(LocalDateTime.now());
            return p;
        });

        PaymentResponse response = paymentService.createPayment(request);

        assertNotNull(response);
        assertEquals(PaymentStatus.PENDING, response.getStatus());
        assertEquals(BigDecimal.valueOf(500000), response.getAmount());
        assertEquals(PaymentMethod.VNPAY, response.getMethod());
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    @DisplayName("Should confirm payment successfully with valid transaction reference")
    void confirmPayment_Success() {
        Payment payment = Payment.builder()
                .id(100L)
                .user(patient)
                .amount(BigDecimal.valueOf(500000))
                .method(PaymentMethod.VNPAY)
                .status(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        PaymentConfirmRequest request = PaymentConfirmRequest.builder()
                .status(PaymentStatus.SUCCESS)
                .transactionRef("VNPAY_TXN_987654321")
                .note("Transaction verified by VNPay webhook")
                .build();

        when(paymentRepository.findByIdWithDetails(100L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        PaymentResponse response = paymentService.confirmPayment(100L, request);

        assertNotNull(response);
        assertEquals(PaymentStatus.SUCCESS, payment.getStatus());
        verify(paymentRepository).save(payment);
    }

    @Test
    @DisplayName("Should reject confirmation if payment is already marked as SUCCESS")
    void confirmPayment_AlreadySuccess_ThrowsException() {
        Payment payment = Payment.builder()
                .id(100L)
                .user(patient)
                .amount(BigDecimal.valueOf(500000))
                .method(PaymentMethod.VNPAY)
                .status(PaymentStatus.SUCCESS)
                .createdAt(LocalDateTime.now())
                .build();

        PaymentConfirmRequest request = PaymentConfirmRequest.builder()
                .status(PaymentStatus.SUCCESS)
                .transactionRef("VNPAY_TXN_987654321")
                .build();

        when(paymentRepository.findByIdWithDetails(100L)).thenReturn(Optional.of(payment));

        assertThrows(BadRequestException.class, () -> paymentService.confirmPayment(100L, request));
    }
}
