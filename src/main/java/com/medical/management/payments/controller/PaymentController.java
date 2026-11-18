package com.medical.management.payments.controller;

import com.medical.management.common.constants.AppConstants;
import com.medical.management.common.response.ApiResponse;
import com.medical.management.common.response.PageResponse;
import com.medical.management.payments.dto.AppointmentInvoicePreviewResponse;
import com.medical.management.payments.dto.CreateAppointmentInvoiceRequest;
import com.medical.management.payments.dto.PaymentConfirmRequest;
import com.medical.management.payments.dto.PaymentCreateRequest;
import com.medical.management.payments.dto.PaymentResponse;
import com.medical.management.payments.dto.RouteFinancialStatisticsResponse;
import com.medical.management.payments.entity.PaymentStatus;
import com.medical.management.payments.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Payment management APIs")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get list of payments with filters and pagination (ADMIN only)")
    public ResponseEntity<ApiResponse<PageResponse<PaymentResponse>>> getPayments(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) PaymentStatus status,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_DIRECTION) String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        PageResponse<PaymentResponse> response = paymentService.getPayments(userId, status, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    @Operation(summary = "Get payment history of a specific patient")
    public ResponseEntity<ApiResponse<PageResponse<PaymentResponse>>> getPaymentsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_DIRECTION) String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        PageResponse<PaymentResponse> response = paymentService.getPaymentsByUserId(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment details by ID")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentById(@PathVariable Long id) {
        PaymentResponse response = paymentService.getPaymentById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    @Operation(summary = "Initiate a payment (Always starts in PENDING status)")
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayment(@Valid @RequestBody PaymentCreateRequest request) {
        PaymentResponse created = paymentService.createPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Payment initiated successfully", created));
    }

    @PostMapping("/{id}/confirm")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Confirm payment status from gateway / webhook / admin verification")
    public ResponseEntity<ApiResponse<PaymentResponse>> confirmPayment(
            @PathVariable Long id,
            @Valid @RequestBody PaymentConfirmRequest request) {
        PaymentResponse confirmed = paymentService.confirmPayment(id, request);
        return ResponseEntity.ok(ApiResponse.success("Payment confirmed successfully", confirmed));
    }

    // =========================================================================
    // ROUTE FINANCIAL STATISTICS & APPOINTMENT INVOICE APIS
    // =========================================================================

    @GetMapping("/route-statistics/{routeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @Operation(summary = "Get financial statistics for a specific treatment route (completed stages cost, invoiced, paid, unpaid)")
    public ResponseEntity<ApiResponse<RouteFinancialStatisticsResponse>> getRouteStatistics(@PathVariable Long routeId) {
        RouteFinancialStatisticsResponse stats = paymentService.getRouteStatistics(routeId);
        return ResponseEntity.ok(ApiResponse.success("Route financial statistics retrieved successfully", stats));
    }

    @GetMapping("/route-statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @Operation(summary = "Get financial statistics for all treatment routes")
    public ResponseEntity<ApiResponse<List<RouteFinancialStatisticsResponse>>> getAllRouteStatistics() {
        List<RouteFinancialStatisticsResponse> stats = paymentService.getAllRouteStatistics();
        return ResponseEntity.ok(ApiResponse.success("All route financial statistics retrieved successfully", stats));
    }

    @GetMapping("/appointment/{appointmentId}/invoice-preview")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @Operation(summary = "Preview invoice for an appointment based on completed stages of linked treatment route")
    public ResponseEntity<ApiResponse<AppointmentInvoicePreviewResponse>> previewAppointmentInvoice(
            @PathVariable Long appointmentId) {
        AppointmentInvoicePreviewResponse preview = paymentService.previewAppointmentInvoice(appointmentId);
        return ResponseEntity.ok(ApiResponse.success("Appointment invoice preview retrieved successfully", preview));
    }

    @PostMapping("/appointment/{appointmentId}/create-invoice")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @Operation(summary = "Create a unique invoice for an appointment calculating cost from completed stages")
    public ResponseEntity<ApiResponse<PaymentResponse>> createInvoiceForAppointment(
            @PathVariable Long appointmentId,
            @Valid @RequestBody CreateAppointmentInvoiceRequest request) {
        PaymentResponse created = paymentService.createInvoiceForAppointment(appointmentId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Invoice created successfully for appointment", created));
    }
}
