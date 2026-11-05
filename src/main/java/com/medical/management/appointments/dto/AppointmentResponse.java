package com.medical.management.appointments.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.medical.management.appointments.entity.Appointment;
import com.medical.management.appointments.entity.AppointmentStatus;
import com.medical.management.doctors.dto.DoctorResponse;
import com.medical.management.services.dto.MedicalServiceResponse;
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
public class AppointmentResponse {

    private Long id;
    private Long userId;
    private UserResponse user;
    private Long doctorId;
    private DoctorResponse doctor;
    private Long serviceId;
    private MedicalServiceResponse service;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime appointmentDate;
    private AppointmentStatus status;
    private String note;
    private String stageName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    public static AppointmentResponse fromEntity(Appointment appointment) {
        if (appointment == null) {
            return null;
        }
        return AppointmentResponse.builder()
                .id(appointment.getId())
                .userId(appointment.getUser() != null ? appointment.getUser().getId() : null)
                .user(appointment.getUser() != null ? UserResponse.fromEntity(appointment.getUser()) : null)
                .doctorId(appointment.getDoctor() != null ? appointment.getDoctor().getId() : null)
                .doctor(appointment.getDoctor() != null ? DoctorResponse.fromEntity(appointment.getDoctor()) : null)
                .serviceId(appointment.getService() != null ? appointment.getService().getId() : null)
                .service(appointment.getService() != null ? MedicalServiceResponse.fromEntity(appointment.getService())
                        : null)
                .appointmentDate(appointment.getAppointmentDate())
                .status(appointment.getStatus())
                .note(appointment.getNote())
                .stageName(appointment.getStageName())
                .createdAt(appointment.getCreatedAt())
                .build();
    }
}
