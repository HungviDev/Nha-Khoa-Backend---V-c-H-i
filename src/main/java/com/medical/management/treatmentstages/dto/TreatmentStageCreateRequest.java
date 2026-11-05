package com.medical.management.treatmentstages.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.medical.management.treatmentstages.entity.TreatmentStageStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class TreatmentStageCreateRequest {

    @NotNull(message = "Treatment route ID is required")
    private Long treatmentRouteId;

    @NotBlank(message = "Stage name cannot be blank")
    @Size(max = 150, message = "Stage name cannot exceed 150 characters")
    private String stageName;

    @NotNull(message = "Sequence order is required")
    @Min(value = 1, message = "Sequence order must be at least 1")
    private Integer sequenceOrder;

    @Builder.Default
    private TreatmentStageStatus status = TreatmentStageStatus.PENDING;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime appointmentDate;

    private String note;

    @DecimalMin(value = "0.0", inclusive = true, message = "Cost must be non-negative")
    @Builder.Default
    private BigDecimal cost = BigDecimal.ZERO;

    @Min(value = 0, message = "Delay must be 0 or positive")
    @Builder.Default
    private Integer delay = 0;
}
