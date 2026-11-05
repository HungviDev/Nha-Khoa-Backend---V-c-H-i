package com.medical.management.doctors.dto;

import com.medical.management.doctors.entity.Doctor;
import com.medical.management.users.dto.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorResponse {

    private Long id;
    private Long userId;
    private String specialization;
    private Integer experienceYears;
    private UserResponse user;

    public static DoctorResponse fromEntity(Doctor doctor) {
        if (doctor == null) {
            return null;
        }
        return DoctorResponse.builder()
                .id(doctor.getId())
                .userId(doctor.getUser() != null ? doctor.getUser().getId() : null)
                .specialization(doctor.getSpecialization())
                .experienceYears(doctor.getExperienceYears())
                .user(doctor.getUser() != null ? UserResponse.fromEntity(doctor.getUser()) : null)
                .build();
    }
}
