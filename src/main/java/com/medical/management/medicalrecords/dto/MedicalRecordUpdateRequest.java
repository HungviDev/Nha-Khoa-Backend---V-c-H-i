package com.medical.management.medicalrecords.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecordUpdateRequest {

    private Long doctorId;

    private Long serviceId;

    private Long treatmentRouteId;

    private String diagnosis;
}
