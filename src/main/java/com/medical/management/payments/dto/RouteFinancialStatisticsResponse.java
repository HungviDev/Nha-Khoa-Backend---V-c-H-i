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
public class RouteFinancialStatisticsResponse {

    private Long routeId;
    private String routeTitle;
    private String routeDescription;
    private int totalStages;
    private int completedStages;
    private int inProgressStages;
    private int pendingStages;
    private BigDecimal totalEstimatedCost;
    private BigDecimal completedCost;
    private BigDecimal pendingCost;
    private BigDecimal totalInvoicedAmount;
    private BigDecimal totalPaidAmount;
    private BigDecimal remainingUnpaidAmount;
    private List<StageFinancialDetail> stages;
}

