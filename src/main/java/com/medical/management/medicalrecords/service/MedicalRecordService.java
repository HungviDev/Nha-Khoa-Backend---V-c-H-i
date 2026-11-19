package com.medical.management.medicalrecords.service;

import com.medical.management.common.response.PageResponse;
import com.medical.management.medicalrecords.dto.MedicalRecordCreateRequest;
import com.medical.management.medicalrecords.dto.MedicalRecordResponse;
import com.medical.management.medicalrecords.dto.MedicalRecordUpdateRequest;
import com.medical.management.treatmentroutes.dto.TreatmentRouteResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MedicalRecordService {

    PageResponse<MedicalRecordResponse> getMedicalRecords(Long userId, Long doctorId, String keyword, Pageable pageable);

    PageResponse<MedicalRecordResponse> getMedicalRecordsByUserId(Long userId, Pageable pageable);

    MedicalRecordResponse getMedicalRecordById(Long id);

    MedicalRecordResponse createMedicalRecord(MedicalRecordCreateRequest request);

    MedicalRecordResponse updateMedicalRecord(Long id, MedicalRecordUpdateRequest request);

    void deleteMedicalRecord(Long id);

    TreatmentRouteResponse getCurrentTreatmentRouteByUserId(Long userId);

    List<TreatmentRouteResponse> getTreatmentRoutesByUserId(Long userId);

    void checkAndClearCompletedTreatmentRoute(Long routeId);
}
