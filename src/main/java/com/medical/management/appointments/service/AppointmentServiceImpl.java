package com.medical.management.appointments.service;

import com.medical.management.appointments.dto.AppointmentCreateRequest;
import com.medical.management.appointments.dto.AppointmentResponse;
import com.medical.management.appointments.dto.AppointmentStatusUpdateRequest;
import com.medical.management.appointments.dto.AppointmentUpdateRequest;
import com.medical.management.appointments.entity.Appointment;
import com.medical.management.appointments.entity.AppointmentStatus;
import com.medical.management.appointments.repository.AppointmentRepository;
import com.medical.management.common.exception.BadRequestException;
import com.medical.management.common.exception.ResourceNotFoundException;
import com.medical.management.common.response.PageResponse;
import com.medical.management.doctors.entity.Doctor;
import com.medical.management.doctors.repository.DoctorRepository;
import com.medical.management.medicalrecords.entity.MedicalRecord;
import com.medical.management.medicalrecords.repository.MedicalRecordRepository;
import com.medical.management.services.entity.MedicalService;
import com.medical.management.services.repository.MedicalServiceRepository;
import com.medical.management.treatmentroutes.entity.TreatmentRoute;
import com.medical.management.treatmentroutes.repository.TreatmentRouteRepository;
import com.medical.management.users.entity.User;
import com.medical.management.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private static final int BUFFER_MINUTES = 30;

    private final AppointmentRepository appointmentRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final MedicalServiceRepository medicalServiceRepository;
    private final TreatmentRouteRepository treatmentRouteRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AppointmentResponse> getAppointments(
            Long userId, Long doctorId, AppointmentStatus status, Pageable pageable) {
        Page<Appointment> page = appointmentRepository.searchAppointments(userId, doctorId, status, pageable);
        return PageResponse.from(page.map(AppointmentResponse::fromEntity));
    }

    @Override
    @Transactional(readOnly = true)
    public AppointmentResponse getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", id));
        return AppointmentResponse.fromEntity(appointment);
    }

    @Override
    @Transactional
    public AppointmentResponse createAppointment(AppointmentCreateRequest request) {
        LocalDateTime appointmentDate = request.getAppointmentDate();
        if (appointmentDate.isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Appointment date must be in the future");
        }

        LocalDateTime slotStart = appointmentDate.minusMinutes(BUFFER_MINUTES);
        LocalDateTime slotEnd = appointmentDate.plusMinutes(BUFFER_MINUTES);

        // Check if doctor has another booking in the same slot
        if (appointmentRepository.existsConflictingDoctorAppointment(request.getDoctorId(), slotStart, slotEnd, null)) {
            throw new BadRequestException("Doctor is already booked for an appointment around " + appointmentDate);
        }

        // Check if patient has another booking in the same slot
        if (appointmentRepository.existsConflictingUserAppointment(request.getUserId(), slotStart, slotEnd, null)) {
            throw new BadRequestException("Patient already has an active appointment around " + appointmentDate);
        }

        User user = userRepository.findByIdWithRole(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));

        Doctor doctor = doctorRepository.findByIdWithUser(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", request.getDoctorId()));

        MedicalService service = medicalServiceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service", "id", request.getServiceId()));

        Appointment appointment = Appointment.builder()
                .user(user)
                .doctor(doctor)
                .service(service)
                .appointmentDate(appointmentDate)
                .status(AppointmentStatus.PENDING)
                .note(request.getNote())
                .stageName(request.getStageName())
                .build();
        Appointment saved = appointmentRepository.save(appointment);
        return AppointmentResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public AppointmentResponse updateAppointment(Long id, AppointmentUpdateRequest request) {
        Appointment appointment = appointmentRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", id));

        if (appointment.getStatus() == AppointmentStatus.CANCELLED
                || appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new BadRequestException("Cannot update appointment with status: " + appointment.getStatus());
        }

        Long targetDoctorId = request.getDoctorId() != null ? request.getDoctorId() : appointment.getDoctor().getId();
        LocalDateTime targetDate = request.getAppointmentDate() != null ? request.getAppointmentDate()
                : appointment.getAppointmentDate();

        if (request.getDoctorId() != null || request.getAppointmentDate() != null) {
            LocalDateTime slotStart = targetDate.minusMinutes(BUFFER_MINUTES);
            LocalDateTime slotEnd = targetDate.plusMinutes(BUFFER_MINUTES);

            if (appointmentRepository.existsConflictingDoctorAppointment(targetDoctorId, slotStart, slotEnd, id)) {
                throw new BadRequestException("Doctor is already booked for an appointment around " + targetDate);
            }

            if (appointmentRepository.existsConflictingUserAppointment(appointment.getUser().getId(), slotStart,
                    slotEnd, id)) {
                throw new BadRequestException("Patient already has an active appointment around " + targetDate);
            }
        }

        if (request.getDoctorId() != null) {
            Doctor doctor = doctorRepository.findByIdWithUser(request.getDoctorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", request.getDoctorId()));
            appointment.setDoctor(doctor);
        }

        if (request.getServiceId() != null) {
            MedicalService service = medicalServiceRepository.findById(request.getServiceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Service", "id", request.getServiceId()));
            appointment.setService(service);
        }

        if (request.getAppointmentDate() != null) {
            appointment.setAppointmentDate(request.getAppointmentDate());
        }

        if (request.getNote() != null) {
            appointment.setNote(request.getNote());
        }

        if (request.getStageName() != null) {
            appointment.setStageName(request.getStageName());
        }

        Appointment updated = appointmentRepository.save(appointment);
        return AppointmentResponse.fromEntity(updated);
    }

    @Override
    @Transactional
    public AppointmentResponse updateAppointmentStatus(Long id, AppointmentStatusUpdateRequest request) {
        Appointment appointment = appointmentRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", id));

        appointment.setStatus(request.getStatus());
        if (request.getNote() != null && !request.getNote().isBlank()) {
            appointment.setNote(appointment.getNote() != null
                    ? appointment.getNote() + "\n[Status Note]: " + request.getNote()
                    : request.getNote());
        }
        User user = userRepository.findByIdWithRole(appointment.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", appointment.getUser().getId()));

        Doctor doctor = doctorRepository.findByIdWithUser(appointment.getDoctor().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", appointment.getDoctor().getId()));

        MedicalService service = medicalServiceRepository.findById(appointment.getService().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Service", "id", appointment.getService().getId()));

        if (request.getStatus() == AppointmentStatus.COMPLETED) {
            // Check if medical record already exists for this appointment (idempotency check)
            MedicalRecord medicalRecord = medicalRecordRepository.findByAppointmentId(appointment.getId())
                    .orElseGet(() -> MedicalRecord.builder()
                            .user(user)
                            .doctor(doctor)
                            .service(service)
                            .appointment(appointment)
                            .build());

            // Determine diagnosis: request.diagnosis -> request.note -> appointment.note -> default fallback
            String diagnosisText = "Đã hoàn thành buổi khám và tư vấn điều trị.";
            if (request.getDiagnosis() != null && !request.getDiagnosis().isBlank()) {
                diagnosisText = request.getDiagnosis().trim();
            } else if (request.getNote() != null && !request.getNote().isBlank()) {
                diagnosisText = request.getNote().trim();
            } else if (appointment.getNote() != null && !appointment.getNote().isBlank()) {
                diagnosisText = appointment.getNote().trim();
            }

            medicalRecord.setDiagnosis(diagnosisText);

            if (request.getTreatmentRouteId() != null) {
                TreatmentRoute route = treatmentRouteRepository.findById(request.getTreatmentRouteId()).orElse(null);
                medicalRecord.setTreatmentRoute(route);
            }

            medicalRecordRepository.save(medicalRecord);
        }
        Appointment updated = appointmentRepository.save(appointment);

        return AppointmentResponse.fromEntity(updated);
    }

    @Override
    @Transactional
    public void cancelAppointment(Long id, String reason) {
        Appointment appointment = appointmentRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", id));

        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new BadRequestException("Cannot cancel an already completed appointment");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        if (reason != null && !reason.isBlank()) {
            appointment.setNote(appointment.getNote() != null
                    ? appointment.getNote() + "\n[Cancellation Reason]: " + reason
                    : "Cancelled: " + reason);
        }
        appointmentRepository.save(appointment);
    }
}
