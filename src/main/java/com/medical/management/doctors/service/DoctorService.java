package com.medical.management.doctors.service;

import com.medical.management.common.response.PageResponse;
import com.medical.management.doctors.dto.DoctorCreateRequest;
import com.medical.management.doctors.dto.DoctorResponse;
import com.medical.management.doctors.dto.DoctorUpdateRequest;
import org.springframework.data.domain.Pageable;

public interface DoctorService {

    PageResponse<DoctorResponse> getDoctors(String keyword, Pageable pageable);

    DoctorResponse getDoctorById(Long id);

    DoctorResponse getDoctorByUserId(Long userId);

    DoctorResponse createDoctor(DoctorCreateRequest request);

    DoctorResponse updateDoctor(Long id, DoctorUpdateRequest request);

    void deleteDoctor(Long id);
}
