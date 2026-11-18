package com.medical.management.payments.dto;

import com.medical.management.payments.entity.PaymentMethod;
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
public class CreateAppointmentInvoiceRequest {

    @NotNull(message = "Payment method is required")
    private PaymentMethod method;

    private BigDecimal customAmount;

    private Long treatmentStageId;
}
