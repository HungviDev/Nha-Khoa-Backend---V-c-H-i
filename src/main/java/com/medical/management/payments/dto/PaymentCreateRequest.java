package com.medical.management.payments.dto;

import com.medical.management.payments.entity.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCreateRequest {

    @NotNull(message = "User (patient) ID is required")
    private Long userId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1000.0", message = "Amount must be at least 1,000 VND")
    private BigDecimal amount;

    @NotNull(message = "Payment method is required")
    private PaymentMethod method;

    private Long treatmentStageId;
}
