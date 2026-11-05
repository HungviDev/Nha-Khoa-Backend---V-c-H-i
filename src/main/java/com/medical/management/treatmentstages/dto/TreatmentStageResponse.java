package com.medical.management.treatmentstages.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.medical.management.treatmentstages.entity.TreatmentStage;
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
public class TreatmentStageResponse {

    private Long id;
    private Long treatmentRouteId;
    private String stageName;
    private Integer sequenceOrder;
    private TreatmentStageStatus status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime appointmentDate;
    private String note;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    private BigDecimal cost;
    private Integer delay;

    public static TreatmentStageResponse fromEntity(TreatmentStage stage) {
        if (stage == null) {
            return null;
        }
        return TreatmentStageResponse.builder()
                .id(stage.getId())
                .treatmentRouteId(stage.getTreatmentRoute() != null ? stage.getTreatmentRoute().getId() : null)
                .stageName(stage.getStageName())
                .sequenceOrder(stage.getSequenceOrder())
                .status(stage.getStatus())
                .appointmentDate(stage.getAppointmentDate())
                .note(stage.getNote())
                .createdAt(stage.getCreatedAt())
                .updatedAt(stage.getUpdatedAt())
                .cost(stage.getCost())
                .delay(stage.getDelay())
                .build();
    }
}
