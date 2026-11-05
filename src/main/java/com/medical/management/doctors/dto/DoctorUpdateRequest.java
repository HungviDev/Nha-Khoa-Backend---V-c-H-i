package com.medical.management.doctors.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorUpdateRequest {

    @Size(max = 100, message = "Specialization cannot exceed 100 characters")
    private String specialization;

    @Min(value = 0, message = "Experience years must be 0 or greater")
    private Integer experienceYears;
}
