package com.medical.management.treatmentstages.service;

import com.medical.management.common.exception.ResourceNotFoundException;
import com.medical.management.treatmentroutes.entity.TreatmentRoute;
import com.medical.management.treatmentroutes.repository.TreatmentRouteRepository;
import com.medical.management.treatmentstages.dto.TreatmentStageCreateRequest;
import com.medical.management.treatmentstages.dto.TreatmentStageResponse;
import com.medical.management.treatmentstages.dto.TreatmentStageUpdateRequest;
import com.medical.management.treatmentstages.entity.TreatmentStage;
import com.medical.management.treatmentstages.entity.TreatmentStageStatus;
import com.medical.management.treatmentstages.repository.TreatmentStageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TreatmentStageServiceImpl implements TreatmentStageService {

    private final TreatmentStageRepository treatmentStageRepository;
    private final TreatmentRouteRepository treatmentRouteRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TreatmentStageResponse> getStagesByRouteId(Long routeId) {
        if (!treatmentRouteRepository.existsById(routeId)) {
            throw new ResourceNotFoundException("TreatmentRoute", "id", routeId);
        }
        return treatmentStageRepository.findByTreatmentRouteIdOrderBySequenceOrderAsc(routeId)
                .stream()
                .map(TreatmentStageResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TreatmentStageResponse getStageById(Long id) {
        TreatmentStage stage = treatmentStageRepository.findByIdWithRoute(id)
                .orElseThrow(() -> new ResourceNotFoundException("TreatmentStage", "id", id));
        return TreatmentStageResponse.fromEntity(stage);
    }

    @Override
    @Transactional
    public TreatmentStageResponse createStage(TreatmentStageCreateRequest request) {
        TreatmentRoute route = treatmentRouteRepository.findById(request.getTreatmentRouteId())
                .orElseThrow(() -> new ResourceNotFoundException("TreatmentRoute", "id", request.getTreatmentRouteId()));

        TreatmentStage stage = TreatmentStage.builder()
                .treatmentRoute(route)
                .stageName(request.getStageName())
                .sequenceOrder(request.getSequenceOrder())
                .status(request.getStatus() != null ? request.getStatus() : TreatmentStageStatus.PENDING)
                .appointmentDate(request.getAppointmentDate())
                .note(request.getNote())
                .cost(request.getCost())
                .delay(request.getDelay())
                .build();

        TreatmentStage saved = treatmentStageRepository.save(stage);
        return TreatmentStageResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public TreatmentStageResponse updateStage(Long id, TreatmentStageUpdateRequest request) {
        TreatmentStage stage = treatmentStageRepository.findByIdWithRoute(id)
                .orElseThrow(() -> new ResourceNotFoundException("TreatmentStage", "id", id));

        if (request.getStageName() != null && !request.getStageName().isBlank()) {
            stage.setStageName(request.getStageName());
        }

        if (request.getSequenceOrder() != null) {
            stage.setSequenceOrder(request.getSequenceOrder());
        }

        if (request.getStatus() != null) {
            stage.setStatus(request.getStatus());
        }

        if (request.getAppointmentDate() != null) {
            stage.setAppointmentDate(request.getAppointmentDate());
        }

        if (request.getNote() != null) {
            stage.setNote(request.getNote());
        }

        if (request.getCost() != null) {
            stage.setCost(request.getCost());
        }

        if (request.getDelay() != null) {
            stage.setDelay(request.getDelay());
        }

        TreatmentStage updated = treatmentStageRepository.save(stage);
        return TreatmentStageResponse.fromEntity(updated);
    }

    @Override
    @Transactional
    public TreatmentStageResponse updateStageStatus(Long id, TreatmentStageStatus status) {
        TreatmentStage stage = treatmentStageRepository.findByIdWithRoute(id)
                .orElseThrow(() -> new ResourceNotFoundException("TreatmentStage", "id", id));

        stage.setStatus(status);
        TreatmentStage updated = treatmentStageRepository.save(stage);
        return TreatmentStageResponse.fromEntity(updated);
    }

    @Override
    @Transactional
    public void deleteStage(Long id) {
        TreatmentStage stage = treatmentStageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TreatmentStage", "id", id));
        treatmentStageRepository.delete(stage);
    }
}
