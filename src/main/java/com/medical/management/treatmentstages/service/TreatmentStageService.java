package com.medical.management.treatmentstages.service;

import com.medical.management.treatmentstages.dto.TreatmentStageCreateRequest;
import com.medical.management.treatmentstages.dto.TreatmentStageResponse;
import com.medical.management.treatmentstages.dto.TreatmentStageUpdateRequest;
import com.medical.management.treatmentstages.entity.TreatmentStageStatus;

import java.util.List;

public interface TreatmentStageService {

    List<TreatmentStageResponse> getStagesByRouteId(Long routeId);

    TreatmentStageResponse getStageById(Long id);

    TreatmentStageResponse createStage(TreatmentStageCreateRequest request);

    TreatmentStageResponse updateStage(Long id, TreatmentStageUpdateRequest request);

    TreatmentStageResponse updateStageStatus(Long id, TreatmentStageStatus status);

    void deleteStage(Long id);
}
