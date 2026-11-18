package com.medical.management.payments.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.medical.management.payments.entity.Payment;
import com.medical.management.payments.entity.PaymentMethod;
import com.medical.management.payments.entity.PaymentStatus;
import com.medical.management.treatmentstages.dto.TreatmentStageResponse;
import com.medical.management.users.dto.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private Long id;
    private Long userId;
    private UserResponse user;
    private Long treatmentRouteId;
    private String treatmentRouteTitle;
    private BigDecimal amount;
    private PaymentMethod method;
    private PaymentStatus status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    private Long treatmentStageId;
    private TreatmentStageResponse treatmentStage;

    public static PaymentResponse fromEntity(Payment payment) {
        if (payment == null) {
            return null;
        }
        Long routeId = null;
        String routeTitle = null;
        if (payment.getTreatmentStage() != null && payment.getTreatmentStage().getTreatmentRoute() != null) {
            routeId = payment.getTreatmentStage().getTreatmentRoute().getId();
            routeTitle = payment.getTreatmentStage().getTreatmentRoute().getTitle();
        }

        return PaymentResponse.builder()
                .id(payment.getId())
                .userId(payment.getUser() != null ? payment.getUser().getId() : null)
                .user(payment.getUser() != null ? UserResponse.fromEntity(payment.getUser()) : null)
                .treatmentRouteId(routeId)
                .treatmentRouteTitle(routeTitle)
                .amount(payment.getAmount())
                .method(payment.getMethod())
                .status(payment.getStatus())
                .createdAt(payment.getCreatedAt())
                .treatmentStageId(payment.getTreatmentStage() != null ? payment.getTreatmentStage().getId() : null)
                .treatmentStage(payment.getTreatmentStage() != null ? TreatmentStageResponse.fromEntity(payment.getTreatmentStage()) : null)
                .build();
    }
}
