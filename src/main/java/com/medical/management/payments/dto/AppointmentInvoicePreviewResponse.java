package com.medical.management.payments.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentInvoicePreviewResponse {

    private Long appointmentId;
    private Long userId;
    private String patientName;
    private String patientPhone;
    private String doctorName;
    private String serviceName;
    private BigDecimal servicePrice;
    private Long treatmentRouteId;
    private String treatmentRouteTitle;
    private List<StageFinancialDetail> completedStages;
    private BigDecimal completedStagesTotalCost;
    private BigDecimal totalCalculatedAmount;
    private boolean alreadyInvoiced;
    private PaymentResponse existingPayment;
}
