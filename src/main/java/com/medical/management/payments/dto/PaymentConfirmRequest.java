package com.medical.management.payments.dto;

import com.medical.management.payments.entity.PaymentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentConfirmRequest {

    @NotNull(message = "Payment status is required")
    private PaymentStatus status;

    @NotBlank(message = "Transaction reference or signature confirmation is required")
    private String transactionRef;

    private String note;
}
