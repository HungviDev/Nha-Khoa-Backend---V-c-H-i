package com.medical.management.medicalrecords.service;

import com.medical.management.common.response.PageResponse;
import com.medical.management.medicalrecords.dto.MedicalRecordCreateRequest;
import com.medical.management.medicalrecords.dto.MedicalRecordResponse;
import com.medical.management.medicalrecords.dto.MedicalRecordUpdateRequest;
import org.springframework.data.domain.Pageable;

public interface MedicalRecordService {

    PageResponse<MedicalRecordResponse> getMedicalRecords(Long userId, Long doctorId, String keyword, Pageable pageable);

    PageResponse<MedicalRecordResponse> getMedicalRecordsByUserId(Long userId, Pageable pageable);

    MedicalRecordResponse getMedicalRecordById(Long id);

    MedicalRecordResponse createMedicalRecord(MedicalRecordCreateRequest request);

    MedicalRecordResponse updateMedicalRecord(Long id, MedicalRecordUpdateRequest request);

    void deleteMedicalRecord(Long id);
}
