package com.medical.management.medicalrecords.service;

import com.medical.management.appointments.entity.Appointment;
import com.medical.management.appointments.repository.AppointmentRepository;
import com.medical.management.common.exception.ResourceNotFoundException;
import com.medical.management.common.response.PageResponse;
import com.medical.management.doctors.entity.Doctor;
import com.medical.management.doctors.repository.DoctorRepository;
import com.medical.management.medicalrecords.dto.MedicalRecordCreateRequest;
import com.medical.management.medicalrecords.dto.MedicalRecordResponse;
import com.medical.management.medicalrecords.dto.MedicalRecordUpdateRequest;
import com.medical.management.medicalrecords.entity.MedicalRecord;
import com.medical.management.medicalrecords.repository.MedicalRecordRepository;
import com.medical.management.services.entity.MedicalService;
import com.medical.management.services.repository.MedicalServiceRepository;
import com.medical.management.treatmentroutes.dto.TreatmentRouteResponse;
import com.medical.management.treatmentroutes.entity.TreatmentRoute;
import com.medical.management.treatmentroutes.repository.TreatmentRouteRepository;
import com.medical.management.treatmentstages.entity.TreatmentStage;
import com.medical.management.treatmentstages.entity.TreatmentStageStatus;
import com.medical.management.treatmentstages.repository.TreatmentStageRepository;
import com.medical.management.users.entity.User;
import com.medical.management.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class MedicalRecordServiceImpl implements MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final MedicalServiceRepository medicalServiceRepository;
    private final TreatmentRouteRepository treatmentRouteRepository;
    private final AppointmentRepository appointmentRepository;
    private final TreatmentStageRepository treatmentStageRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MedicalRecordResponse> getMedicalRecords(
            Long userId, Long doctorId, String keyword, Pageable pageable) {
        Page<MedicalRecord> page = medicalRecordRepository.searchMedicalRecords(userId, doctorId, keyword, pageable);
        return PageResponse.from(page.map(MedicalRecordResponse::fromEntity));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MedicalRecordResponse> getMedicalRecordsByUserId(Long userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        Page<MedicalRecord> page = medicalRecordRepository.findByUserId(userId, pageable);
        return PageResponse.from(page.map(MedicalRecordResponse::fromEntity));
    }

    @Override
    @Transactional(readOnly = true)
    public MedicalRecordResponse getMedicalRecordById(Long id) {
        MedicalRecord record = medicalRecordRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("MedicalRecord", "id", id));
        return MedicalRecordResponse.fromEntity(record);
    }

    @Override
    @Transactional
    public MedicalRecordResponse createMedicalRecord(MedicalRecordCreateRequest request) {
        User user = userRepository.findByIdWithRole(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));

        Doctor doctor = doctorRepository.findByIdWithUser(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", request.getDoctorId()));

        MedicalService service = medicalServiceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service", "id", request.getServiceId()));

        TreatmentRoute treatmentRoute = null;
        if (request.getTreatmentRouteId() != null) {
            treatmentRoute = treatmentRouteRepository.findById(request.getTreatmentRouteId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("TreatmentRoute", "id", request.getTreatmentRouteId()));
        }

        Appointment appointment = null;
        if (request.getAppointmentId() != null) {
            appointment = appointmentRepository.findById(request.getAppointmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", request.getAppointmentId()));
        }

        MedicalRecord record = MedicalRecord.builder()
                .user(user)
                .doctor(doctor)
                .service(service)
                .treatmentRoute(treatmentRoute)
                .appointment(appointment)
                .diagnosis(request.getDiagnosis())
                .build();

        MedicalRecord saved = medicalRecordRepository.save(record);
        return MedicalRecordResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public MedicalRecordResponse updateMedicalRecord(Long id, MedicalRecordUpdateRequest request) {
        MedicalRecord record = medicalRecordRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("MedicalRecord", "id", id));

        if (request.getDoctorId() != null) {
            Doctor doctor = doctorRepository.findByIdWithUser(request.getDoctorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", request.getDoctorId()));
            record.setDoctor(doctor);
        }

        if (request.getServiceId() != null) {
            MedicalService service = medicalServiceRepository.findById(request.getServiceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Service", "id", request.getServiceId()));
            record.setService(service);
        }

        if (request.getTreatmentRouteId() != null) {
            TreatmentRoute treatmentRoute = treatmentRouteRepository.findById(request.getTreatmentRouteId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("TreatmentRoute", "id", request.getTreatmentRouteId()));
            record.setTreatmentRoute(treatmentRoute);
        }

        if (request.getDiagnosis() != null && !request.getDiagnosis().isBlank()) {
            record.setDiagnosis(request.getDiagnosis());
        }

        MedicalRecord updated = medicalRecordRepository.save(record);
        return MedicalRecordResponse.fromEntity(updated);
    }

    @Override
    @Transactional
    public void deleteMedicalRecord(Long id) {
        MedicalRecord record = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MedicalRecord", "id", id));
        medicalRecordRepository.delete(record);
    }

    @Override
    @Transactional(readOnly = true)
    public TreatmentRouteResponse getCurrentTreatmentRouteByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        return medicalRecordRepository.findFirstByUserIdAndTreatmentRouteIsNotNullOrderByIdDesc(userId)
                .map(MedicalRecord::getTreatmentRoute)
                .map(TreatmentRouteResponse::fromEntity)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TreatmentRouteResponse> getTreatmentRoutesByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        List<MedicalRecord> records = medicalRecordRepository.findByUserIdAndTreatmentRouteIsNotNullOrderByIdDesc(userId);
        Set<Long> seenIds = new LinkedHashSet<>();
        List<TreatmentRouteResponse> results = new ArrayList<>();
        for (MedicalRecord record : records) {
            TreatmentRoute route = record.getTreatmentRoute();
            if (route != null && seenIds.add(route.getId())) {
                results.add(TreatmentRouteResponse.fromEntity(route));
            }
        }
        return results;
    }

    @Override
    @Transactional
    public void checkAndClearCompletedTreatmentRoute(Long routeId) {
        if (routeId == null) {
            return;
        }
        List<TreatmentStage> allStages = treatmentStageRepository.findByTreatmentRouteIdOrderBySequenceOrderAsc(routeId);
        if (allStages.isEmpty()) {
            return;
        }

        boolean allCompleted = allStages.stream()
                .allMatch(s -> s.getStatus() == TreatmentStageStatus.COMPLETED);

        if (allCompleted) {
            List<MedicalRecord> records = medicalRecordRepository.findByTreatmentRouteId(routeId);
            if (!records.isEmpty()) {
                for (MedicalRecord record : records) {
                    record.setTreatmentRoute(null);
                }
                medicalRecordRepository.saveAll(records);
                log.info("Lộ trình ID {} hoàn tất tất cả các giai đoạn. Đã set NULL cột treatment_route_id trong {} hồ sơ bệnh án.",
                        routeId, records.size());
            }
        }
    }
}
