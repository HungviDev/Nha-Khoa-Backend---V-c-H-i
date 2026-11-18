package com.medical.management.payments.dto;

import com.medical.management.treatmentstages.entity.TreatmentStageStatus;
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
public class StageFinancialDetail {

    private Long stageId;
    private String stageName;
    private Integer sequenceOrder;
    private TreatmentStageStatus status;
    private BigDecimal cost;
    private Integer delay;
    private LocalDateTime appointmentDate;
    private String note;
    private boolean isCompleted;
    private boolean isInvoiced;
    private Long paymentId;
}
