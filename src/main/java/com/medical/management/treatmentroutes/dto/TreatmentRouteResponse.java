package com.medical.management.treatmentroutes.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.medical.management.treatmentroutes.entity.TreatmentRoute;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TreatmentRouteResponse {

    private Long id;
    private String title;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    public static TreatmentRouteResponse fromEntity(TreatmentRoute route) {
        if (route == null) {
            return null;
        }
        return TreatmentRouteResponse.builder()
                .id(route.getId())
                .title(route.getTitle())
                .description(route.getDescription())
                .createdAt(route.getCreatedAt())
                .build();
    }
}
