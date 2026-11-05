package com.medical.management.treatmentstages.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.medical.management.treatmentstages.entity.TreatmentStageStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
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
public class TreatmentStageUpdateRequest {

    @Size(max = 150, message = "Stage name cannot exceed 150 characters")
    private String stageName;

    @Min(value = 1, message = "Sequence order must be at least 1")
    private Integer sequenceOrder;

    private TreatmentStageStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime appointmentDate;

    private String note;

    @DecimalMin(value = "0.0", inclusive = true, message = "Cost must be non-negative")
    private BigDecimal cost;

    @Min(value = 0, message = "Delay must be 0 or positive")
    private Integer delay;
}
