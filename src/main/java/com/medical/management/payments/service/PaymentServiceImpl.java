package com.medical.management.payments.service;

import com.medical.management.appointments.entity.Appointment;
import com.medical.management.appointments.repository.AppointmentRepository;
import com.medical.management.common.exception.BadRequestException;
import com.medical.management.common.exception.ResourceNotFoundException;
import com.medical.management.common.response.PageResponse;
import com.medical.management.medicalrecords.entity.MedicalRecord;
import com.medical.management.medicalrecords.repository.MedicalRecordRepository;
import com.medical.management.payments.dto.AppointmentInvoicePreviewResponse;
import com.medical.management.payments.dto.CreateAppointmentInvoiceRequest;
import com.medical.management.payments.dto.PaymentConfirmRequest;
import com.medical.management.payments.dto.PaymentCreateRequest;
import com.medical.management.payments.dto.PaymentResponse;
import com.medical.management.payments.dto.RouteFinancialStatisticsResponse;
import com.medical.management.payments.dto.StageFinancialDetail;
import com.medical.management.payments.entity.Payment;
import com.medical.management.payments.entity.PaymentStatus;
import com.medical.management.payments.repository.PaymentRepository;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final TreatmentStageRepository treatmentStageRepository;
    private final TreatmentRouteRepository treatmentRouteRepository;
    private final AppointmentRepository appointmentRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PaymentResponse> getPayments(Long userId, PaymentStatus status, Pageable pageable) {
        Page<Payment> page = paymentRepository.searchPayments(userId, status, pageable);
        return PageResponse.from(page.map(PaymentResponse::fromEntity));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PaymentResponse> getPaymentsByUserId(Long userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        Page<Payment> page = paymentRepository.findByUserId(userId, pageable);
        return PageResponse.from(page.map(PaymentResponse::fromEntity));
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long id) {
        Payment payment = paymentRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));
        return PaymentResponse.fromEntity(payment);
    }

    @Override
    @Transactional
    public PaymentResponse createPayment(PaymentCreateRequest request) {
        User user = userRepository.findByIdWithRole(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));

        TreatmentStage treatmentStage = null;
        if (request.getTreatmentStageId() != null) {
            treatmentStage = treatmentStageRepository.findByIdWithRoute(request.getTreatmentStageId())
                    .orElseThrow(() -> new ResourceNotFoundException("TreatmentStage", "id", request.getTreatmentStageId()));
        }

        Payment payment = Payment.builder()
                .user(user)
                .amount(request.getAmount())
                .method(request.getMethod())
                .status(PaymentStatus.PENDING)
                .treatmentStage(treatmentStage)
                .build();

        Payment saved = paymentRepository.save(payment);
        log.info("Payment initiated with ID: {}, amount: {}, method: {}", saved.getId(), saved.getAmount(), saved.getMethod());
        return PaymentResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public PaymentResponse confirmPayment(Long id, PaymentConfirmRequest request) {
        Payment payment = paymentRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            throw new BadRequestException("Payment is already confirmed as SUCCESS");
        }

        if (request.getTransactionRef() == null || request.getTransactionRef().isBlank()) {
            throw new BadRequestException("Valid transaction reference or verification signature is required");
        }

        payment.setStatus(request.getStatus());
        Payment updated = paymentRepository.save(payment);
        log.info("Payment ID: {} status updated to: {} with ref: {}", id, request.getStatus(), request.getTransactionRef());

        return PaymentResponse.fromEntity(updated);
    }

    // =========================================================================
    // FINANCIAL STATISTICS PER TREATMENT ROUTE (CHECK COMPLETED STAGES)
    // =========================================================================

    @Override
    @Transactional(readOnly = true)
    public RouteFinancialStatisticsResponse getRouteStatistics(Long routeId) {
        TreatmentRoute route = treatmentRouteRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("TreatmentRoute", "id", routeId));
        return calculateRouteStatistics(route);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RouteFinancialStatisticsResponse> getAllRouteStatistics() {
        List<TreatmentRoute> routes = treatmentRouteRepository.findAll();
        return routes.stream()
                .map(this::calculateRouteStatistics)
                .collect(Collectors.toList());
    }

    private RouteFinancialStatisticsResponse calculateRouteStatistics(TreatmentRoute route) {
        List<TreatmentStage> stages = treatmentStageRepository.findByTreatmentRouteIdOrderBySequenceOrderAsc(route.getId());
        List<Payment> payments = paymentRepository.findByRouteId(route.getId());

        // Map stageId -> Payment if payment exists specifically for that stage
        Map<Long, Payment> stagePaymentMap = payments.stream()
                .filter(p -> p.getTreatmentStage() != null)
                .collect(Collectors.toMap(
                        p -> p.getTreatmentStage().getId(),
                        Function.identity(),
                        (existing, replacement) -> existing
                ));

        int totalStages = stages.size();
        int completedStages = 0;
        int inProgressStages = 0;
        int pendingStages = 0;

        BigDecimal totalEstimatedCost = BigDecimal.ZERO;
        BigDecimal completedCost = BigDecimal.ZERO;

        List<StageFinancialDetail> stageDetails = new ArrayList<>();

        for (TreatmentStage stage : stages) {
            BigDecimal cost = stage.getCost() != null ? stage.getCost() : BigDecimal.ZERO;
            totalEstimatedCost = totalEstimatedCost.add(cost);

            boolean isCompleted = stage.getStatus() == TreatmentStageStatus.COMPLETED;
            if (isCompleted) {
                completedStages++;
                completedCost = completedCost.add(cost);
            } else if (stage.getStatus() == TreatmentStageStatus.IN_PROGRESS) {
                inProgressStages++;
            } else if (stage.getStatus() == TreatmentStageStatus.PENDING) {
                pendingStages++;
            }

            Payment stagePayment = stagePaymentMap.get(stage.getId());
            boolean isInvoiced = stagePayment != null;

            stageDetails.add(StageFinancialDetail.builder()
                    .stageId(stage.getId())
                    .stageName(stage.getStageName())
                    .sequenceOrder(stage.getSequenceOrder())
                    .status(stage.getStatus())
                    .cost(cost)
                    .delay(stage.getDelay())
                    .appointmentDate(stage.getAppointmentDate())
                    .note(stage.getNote())
                    .isCompleted(isCompleted)
                    .isInvoiced(isInvoiced)
                    .paymentId(stagePayment != null ? stagePayment.getId() : null)
                    .build());
        }

        BigDecimal pendingCost = totalEstimatedCost.subtract(completedCost).max(BigDecimal.ZERO);
        BigDecimal totalInvoicedAmount = payments.stream()
                .map(p -> p.getAmount() != null ? p.getAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalPaidAmount = payments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.SUCCESS)
                .map(p -> p.getAmount() != null ? p.getAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal remainingUnpaidAmount = completedCost.subtract(totalPaidAmount).max(BigDecimal.ZERO);

        return RouteFinancialStatisticsResponse.builder()
                .routeId(route.getId())
                .routeTitle(route.getTitle())
                .routeDescription(route.getDescription())
                .totalStages(totalStages)
                .completedStages(completedStages)
                .inProgressStages(inProgressStages)
                .pendingStages(pendingStages)
                .totalEstimatedCost(totalEstimatedCost)
                .completedCost(completedCost)
                .pendingCost(pendingCost)
                .totalInvoicedAmount(totalInvoicedAmount)
                .totalPaidAmount(totalPaidAmount)
                .remainingUnpaidAmount(remainingUnpaidAmount)
                .stages(stageDetails)
                .build();
    }

    // =========================================================================
    // APPOINTMENT INVOICE PREVIEW & GENERATION (CHECK COMPLETED STAGES)
    // =========================================================================

    @Override
    @Transactional(readOnly = true)
    public AppointmentInvoicePreviewResponse previewAppointmentInvoice(Long appointmentId) {
        Appointment appointment = appointmentRepository.findByIdWithDetails(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", appointmentId));

        // Find linked medical record
        Optional<MedicalRecord> recordOpt = medicalRecordRepository.findByAppointmentId(appointmentId);

        TreatmentRoute route = null;
        List<StageFinancialDetail> completedStages = new ArrayList<>();
        BigDecimal completedStagesTotalCost = BigDecimal.ZERO;
        Payment existingPayment = null;

        if (recordOpt.isPresent() && recordOpt.get().getTreatmentRoute() != null) {
            route = recordOpt.get().getTreatmentRoute();
            List<TreatmentStage> stages = treatmentStageRepository.findByTreatmentRouteIdOrderBySequenceOrderAsc(route.getId());
            for (TreatmentStage stage : stages) {
                if (stage.getStatus() == TreatmentStageStatus.COMPLETED) {
                    BigDecimal cost = stage.getCost() != null ? stage.getCost() : BigDecimal.ZERO;
                    completedStagesTotalCost = completedStagesTotalCost.add(cost);

                    Optional<Payment> stagePaymentOpt = paymentRepository.findByTreatmentStageId(stage.getId());
                    boolean isInvoiced = stagePaymentOpt.isPresent();
                    if (isInvoiced && existingPayment == null) {
                        existingPayment = stagePaymentOpt.get();
                    }

                    completedStages.add(StageFinancialDetail.builder()
                            .stageId(stage.getId())
                            .stageName(stage.getStageName())
                            .sequenceOrder(stage.getSequenceOrder())
                            .status(stage.getStatus())
                            .cost(cost)
                            .appointmentDate(stage.getAppointmentDate())
                            .note(stage.getNote())
                            .isCompleted(true)
                            .isInvoiced(isInvoiced)
                            .paymentId(stagePaymentOpt.map(Payment::getId).orElse(null))
                            .build());
                }
            }
        }

        BigDecimal servicePrice = (appointment.getService() != null && appointment.getService().getPrice() != null)
                ? appointment.getService().getPrice()
                : BigDecimal.ZERO;

        BigDecimal totalCalculatedAmount;
        if (completedStagesTotalCost.compareTo(BigDecimal.ZERO) > 0) {
            totalCalculatedAmount = completedStagesTotalCost;
        } else {
            totalCalculatedAmount = servicePrice;
        }

        return AppointmentInvoicePreviewResponse.builder()
                .appointmentId(appointment.getId())
                .userId(appointment.getUser().getId())
                .patientName(appointment.getUser().getFullname())
                .patientPhone(appointment.getUser().getPhone())
                .doctorName(appointment.getDoctor() != null && appointment.getDoctor().getUser() != null
                        ? appointment.getDoctor().getUser().getFullname() : null)
                .serviceName(appointment.getService() != null ? appointment.getService().getName() : null)
                .servicePrice(servicePrice)
                .treatmentRouteId(route != null ? route.getId() : null)
                .treatmentRouteTitle(route != null ? route.getTitle() : null)
                .completedStages(completedStages)
                .completedStagesTotalCost(completedStagesTotalCost)
                .totalCalculatedAmount(totalCalculatedAmount)
                .alreadyInvoiced(existingPayment != null)
                .existingPayment(PaymentResponse.fromEntity(existingPayment))
                .build();
    }

    @Override
    @Transactional
    public PaymentResponse createInvoiceForAppointment(Long appointmentId, CreateAppointmentInvoiceRequest request) {
        Appointment appointment = appointmentRepository.findByIdWithDetails(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", appointmentId));

        // Find linked medical record
        Optional<MedicalRecord> recordOpt = medicalRecordRepository.findByAppointmentId(appointmentId);
        TreatmentRoute route = recordOpt.map(MedicalRecord::getTreatmentRoute).orElse(null);

        BigDecimal completedStagesCost = BigDecimal.ZERO;
        TreatmentStage lastCompletedStage = null;

        if (route != null) {
            List<TreatmentStage> stages = treatmentStageRepository.findByTreatmentRouteIdOrderBySequenceOrderAsc(route.getId());
            for (TreatmentStage stage : stages) {
                if (stage.getStatus() == TreatmentStageStatus.COMPLETED) {
                    completedStagesCost = completedStagesCost.add(stage.getCost() != null ? stage.getCost() : BigDecimal.ZERO);
                    lastCompletedStage = stage;
                }
            }
        }

        // Determine attached stage
        TreatmentStage stageToAttach = null;
        if (request.getTreatmentStageId() != null) {
            stageToAttach = treatmentStageRepository.findById(request.getTreatmentStageId()).orElse(null);
        } else {
            stageToAttach = lastCompletedStage;
        }

        // Check if an existing payment exists for this stage
        if (stageToAttach != null) {
            Optional<Payment> existingOpt = paymentRepository.findByTreatmentStageId(stageToAttach.getId());
            if (existingOpt.isPresent()) {
                Payment existing = existingOpt.get();
                if (existing.getStatus() == PaymentStatus.SUCCESS) {
                    throw new BadRequestException("Giai đoạn '" + stageToAttach.getStageName() + "' đã có hóa đơn đã thanh toán thành công (Mã HĐ #" + existing.getId() + ")");
                }
            }
        }

        // Determine final amount
        BigDecimal finalAmount;
        if (request.getCustomAmount() != null && request.getCustomAmount().compareTo(BigDecimal.ZERO) > 0) {
            finalAmount = request.getCustomAmount();
        } else if (completedStagesCost.compareTo(BigDecimal.ZERO) > 0) {
            finalAmount = completedStagesCost;
        } else if (appointment.getService() != null && appointment.getService().getPrice() != null) {
            finalAmount = appointment.getService().getPrice();
        } else {
            finalAmount = new BigDecimal("1000"); // Minimum valid amount
        }

        Payment payment = Payment.builder()
                .user(appointment.getUser())
                .treatmentStage(stageToAttach)
                .amount(finalAmount)
                .method(request.getMethod())
                .status(PaymentStatus.PENDING)
                .build();

        Payment saved = paymentRepository.save(payment);
        log.info("Invoice created with ID: #{} for Appointment #{}, amount: {}, route: {}", 
                saved.getId(), appointmentId, saved.getAmount(), route != null ? route.getTitle() : "N/A");

        return PaymentResponse.fromEntity(saved);
    }
}
