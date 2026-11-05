package com.medical.management.treatmentroutes.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TreatmentRouteUpdateRequest {

    @Size(max = 200, message = "Title cannot exceed 200 characters")
    private String title;

    private String description;
}
