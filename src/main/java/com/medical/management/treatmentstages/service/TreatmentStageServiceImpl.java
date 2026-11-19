package com.medical.management.treatmentstages.service;

import com.medical.management.common.exception.ResourceNotFoundException;
import com.medical.management.medicalrecords.entity.MedicalRecord;
import com.medical.management.medicalrecords.repository.MedicalRecordRepository;
import com.medical.management.treatmentroutes.entity.TreatmentRoute;
import com.medical.management.treatmentroutes.repository.TreatmentRouteRepository;
import com.medical.management.treatmentstages.dto.TreatmentStageCreateRequest;
import com.medical.management.treatmentstages.dto.TreatmentStageResponse;
import com.medical.management.treatmentstages.dto.TreatmentStageUpdateRequest;
import com.medical.management.treatmentstages.entity.TreatmentStage;
import com.medical.management.treatmentstages.entity.TreatmentStageStatus;
import com.medical.management.treatmentstages.repository.TreatmentStageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TreatmentStageServiceImpl implements TreatmentStageService {

    private final TreatmentStageRepository treatmentStageRepository;
    private final TreatmentRouteRepository treatmentRouteRepository;
    private final MedicalRecordRepository medicalRecordRepository;

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
        checkAndClearCompletedTreatmentRoute(stage.getTreatmentRoute());
        return TreatmentStageResponse.fromEntity(updated);
    }

    @Override
    @Transactional
    public TreatmentStageResponse updateStageStatus(Long id, TreatmentStageStatus status) {
        TreatmentStage stage = treatmentStageRepository.findByIdWithRoute(id)
                .orElseThrow(() -> new ResourceNotFoundException("TreatmentStage", "id", id));

        stage.setStatus(status);
        TreatmentStage updated = treatmentStageRepository.save(stage);
        checkAndClearCompletedTreatmentRoute(stage.getTreatmentRoute());
        return TreatmentStageResponse.fromEntity(updated);
    }

    @Override
    @Transactional
    public void deleteStage(Long id) {
        TreatmentStage stage = treatmentStageRepository.findByIdWithRoute(id)
                .orElseThrow(() -> new ResourceNotFoundException("TreatmentStage", "id", id));
        TreatmentRoute route = stage.getTreatmentRoute();
        treatmentStageRepository.delete(stage);
        checkAndClearCompletedTreatmentRoute(route);
    }

    private void checkAndClearCompletedTreatmentRoute(TreatmentRoute route) {
        if (route == null || route.getId() == null) {
            return;
        }
        Long routeId = route.getId();
        List<TreatmentStage> allStages = treatmentStageRepository.findByTreatmentRouteIdOrderBySequenceOrderAsc(routeId);
        if (allStages.isEmpty()) {
            return;
        }

        // Check if all stages belonging to this treatment route are COMPLETED
        boolean allCompleted = allStages.stream()
                .allMatch(s -> s.getStatus() == TreatmentStageStatus.COMPLETED);

        if (allCompleted) {
            List<MedicalRecord> records = medicalRecordRepository.findByTreatmentRouteId(routeId);
            if (!records.isEmpty()) {
                for (MedicalRecord record : records) {
                    record.setTreatmentRoute(null);
                }
                medicalRecordRepository.saveAll(records);
                log.info("Lộ trình điều trị ID {} đã hoàn tất toàn bộ giai đoạn. Đã set null cột treatment_route_id cho {} hồ sơ bệnh án liên quan.",
                        routeId, records.size());
            }
        }
    }
}
