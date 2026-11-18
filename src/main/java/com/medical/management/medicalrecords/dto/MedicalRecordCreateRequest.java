package com.medical.management.medicalrecords.dto;

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
public class MedicalRecordCreateRequest {

    @NotNull(message = "User (patient) ID is required")
    private Long userId;

    @NotNull(message = "Doctor ID is required")
    private Long doctorId;

    @NotNull(message = "Service ID is required")
    private Long serviceId;

    private Long treatmentRouteId;

    private Long appointmentId;

    @NotBlank(message = "Diagnosis cannot be blank")
    private String diagnosis;
}
