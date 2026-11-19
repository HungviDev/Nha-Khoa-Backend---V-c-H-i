package com.medical.management.medicalrecords.controller;

import com.medical.management.common.constants.AppConstants;
import com.medical.management.common.response.ApiResponse;
import com.medical.management.common.response.PageResponse;
import com.medical.management.medicalrecords.dto.MedicalRecordCreateRequest;
import com.medical.management.medicalrecords.dto.MedicalRecordResponse;
import com.medical.management.medicalrecords.dto.MedicalRecordUpdateRequest;
import com.medical.management.medicalrecords.service.MedicalRecordService;
import com.medical.management.security.service.UserPrincipal;
import com.medical.management.treatmentroutes.dto.TreatmentRouteResponse;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/medical-records")
@RequiredArgsConstructor
@Tag(name = "Medical Records", description = "Medical record management APIs")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @Operation(summary = "Get list of medical records with filters and pagination (ADMIN/DOCTOR)")
    public ResponseEntity<ApiResponse<PageResponse<MedicalRecordResponse>>> getMedicalRecords(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_DIRECTION) String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        PageResponse<MedicalRecordResponse> response = medicalRecordService.getMedicalRecords(userId, doctorId, keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/patient/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR') or #userId == authentication.principal.id")
    @Operation(summary = "Get medical record history by patient ID")
    public ResponseEntity<ApiResponse<PageResponse<MedicalRecordResponse>>> getMedicalRecordsByPatient(
            @PathVariable Long userId,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_DIRECTION) String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        PageResponse<MedicalRecordResponse> response = medicalRecordService.getMedicalRecordsByUserId(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get medical record details by ID")
    public ResponseEntity<ApiResponse<MedicalRecordResponse>> getMedicalRecordById(@PathVariable Long id) {
        MedicalRecordResponse response = medicalRecordService.getMedicalRecordById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @Operation(summary = "Create medical record (ADMIN/DOCTOR)")
    public ResponseEntity<ApiResponse<MedicalRecordResponse>> createMedicalRecord(
            @Valid @RequestBody MedicalRecordCreateRequest request) {
        MedicalRecordResponse created = medicalRecordService.createMedicalRecord(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Medical record created successfully", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @Operation(summary = "Update medical record (ADMIN/DOCTOR)")
    public ResponseEntity<ApiResponse<MedicalRecordResponse>> updateMedicalRecord(
            @PathVariable Long id,
            @Valid @RequestBody MedicalRecordUpdateRequest request) {
        MedicalRecordResponse updated = medicalRecordService.updateMedicalRecord(id, request);
        return ResponseEntity.ok(ApiResponse.success("Medical record updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete medical record (ADMIN only)")
    public ResponseEntity<ApiResponse<Void>> deleteMedicalRecord(@PathVariable Long id) {
        medicalRecordService.deleteMedicalRecord(id);
        return ResponseEntity.ok(ApiResponse.success("Medical record deleted successfully", null));
    }

    @GetMapping("/patient/current-treatment-route")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get current treatment route of logged-in patient")
    public ResponseEntity<ApiResponse<TreatmentRouteResponse>> getMyCurrentTreatmentRoute(
            @AuthenticationPrincipal UserPrincipal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Unauthorized"));
        }
        TreatmentRouteResponse response = medicalRecordService.getCurrentTreatmentRouteByUserId(principal.getId());
        return ResponseEntity.ok(ApiResponse.success(
                response != null ? "Current treatment route retrieved successfully" : "No treatment route found for patient",
                response
        ));
    }

    @GetMapping("/patient/{userId}/current-treatment-route")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR') or #userId == authentication.principal.id")
    @Operation(summary = "Get current treatment route of a patient by ID")
    public ResponseEntity<ApiResponse<TreatmentRouteResponse>> getCurrentTreatmentRouteByPatient(
            @PathVariable Long userId) {
        TreatmentRouteResponse response = medicalRecordService.getCurrentTreatmentRouteByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(
                response != null ? "Current treatment route retrieved successfully" : "No treatment route found for patient",
                response
        ));
    }

    @GetMapping("/patient/treatment-routes")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all treatment routes of logged-in patient")
    public ResponseEntity<ApiResponse<List<TreatmentRouteResponse>>> getMyTreatmentRoutes(
            @AuthenticationPrincipal UserPrincipal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Unauthorized"));
        }
        List<TreatmentRouteResponse> response = medicalRecordService.getTreatmentRoutesByUserId(principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Treatment routes retrieved successfully", response));
    }

    @GetMapping("/patient/{userId}/treatment-routes")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR') or #userId == authentication.principal.id")
    @Operation(summary = "Get all treatment routes of a patient by ID")
    public ResponseEntity<ApiResponse<List<TreatmentRouteResponse>>> getTreatmentRoutesByPatient(
            @PathVariable Long userId) {
        List<TreatmentRouteResponse> response = medicalRecordService.getTreatmentRoutesByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("Treatment routes retrieved successfully", response));
    }

    @PostMapping("/route/{routeId}/check-completion")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @Operation(summary = "Check and clear treatment_route_id in medical records if all stages of the route are completed")
    public ResponseEntity<ApiResponse<Void>> checkAndClearCompletedRoute(@PathVariable Long routeId) {
        medicalRecordService.checkAndClearCompletedTreatmentRoute(routeId);
        return ResponseEntity.ok(ApiResponse.success("Treatment route completion checked and updated successfully", null));
    }
}
