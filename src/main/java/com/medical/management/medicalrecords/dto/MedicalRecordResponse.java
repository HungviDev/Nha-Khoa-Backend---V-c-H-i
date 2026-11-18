package com.medical.management.medicalrecords.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.medical.management.doctors.dto.DoctorResponse;
import com.medical.management.medicalrecords.entity.MedicalRecord;
import com.medical.management.services.dto.MedicalServiceResponse;
import com.medical.management.treatmentroutes.dto.TreatmentRouteResponse;
import com.medical.management.users.dto.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecordResponse {

    private Long id;
    private Long userId;
    private UserResponse user;
    private Long doctorId;
    private DoctorResponse doctor;
    private Long serviceId;
    private MedicalServiceResponse service;
    private Long treatmentRouteId;
    private TreatmentRouteResponse treatmentRoute;
    private Long appointmentId;
    private String diagnosis;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    public static MedicalRecordResponse fromEntity(MedicalRecord record) {
        if (record == null) {
            return null;
        }
        return MedicalRecordResponse.builder()
                .id(record.getId())
                .userId(record.getUser() != null ? record.getUser().getId() : null)
                .user(record.getUser() != null ? UserResponse.fromEntity(record.getUser()) : null)
                .doctorId(record.getDoctor() != null ? record.getDoctor().getId() : null)
                .doctor(record.getDoctor() != null ? DoctorResponse.fromEntity(record.getDoctor()) : null)
                .serviceId(record.getService() != null ? record.getService().getId() : null)
                .service(record.getService() != null ? MedicalServiceResponse.fromEntity(record.getService()) : null)
                .treatmentRouteId(record.getTreatmentRoute() != null ? record.getTreatmentRoute().getId() : null)
                .treatmentRoute(record.getTreatmentRoute() != null ? TreatmentRouteResponse.fromEntity(record.getTreatmentRoute()) : null)
                .appointmentId(record.getAppointment() != null ? record.getAppointment().getId() : null)
                .diagnosis(record.getDiagnosis())
                .createdAt(record.getCreatedAt())
                .build();
    }
}
