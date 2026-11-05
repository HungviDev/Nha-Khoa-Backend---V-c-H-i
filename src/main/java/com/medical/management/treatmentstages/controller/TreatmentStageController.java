package com.medical.management.treatmentstages.controller;

import com.medical.management.common.response.ApiResponse;
import com.medical.management.treatmentstages.dto.TreatmentStageCreateRequest;
import com.medical.management.treatmentstages.dto.TreatmentStageResponse;
import com.medical.management.treatmentstages.dto.TreatmentStageUpdateRequest;
import com.medical.management.treatmentstages.entity.TreatmentStageStatus;
import com.medical.management.treatmentstages.service.TreatmentStageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/treatment-stages")
@RequiredArgsConstructor
@Tag(name = "Treatment Stages", description = "Treatment stage management APIs")
public class TreatmentStageController {

    private final TreatmentStageService treatmentStageService;

    @GetMapping("/route/{routeId}")
    @Operation(summary = "Get stages for a treatment route ordered by sequence")
    public ResponseEntity<ApiResponse<List<TreatmentStageResponse>>> getStagesByRouteId(@PathVariable Long routeId) {
        List<TreatmentStageResponse> stages = treatmentStageService.getStagesByRouteId(routeId);
        return ResponseEntity.ok(ApiResponse.success(stages));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get treatment stage details by ID")
    public ResponseEntity<ApiResponse<TreatmentStageResponse>> getStageById(@PathVariable Long id) {
        TreatmentStageResponse response = treatmentStageService.getStageById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @Operation(summary = "Create treatment stage (ADMIN/DOCTOR)")
    public ResponseEntity<ApiResponse<TreatmentStageResponse>> createStage(
            @Valid @RequestBody TreatmentStageCreateRequest request) {
        TreatmentStageResponse created = treatmentStageService.createStage(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Treatment stage created successfully", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @Operation(summary = "Update treatment stage (ADMIN/DOCTOR)")
    public ResponseEntity<ApiResponse<TreatmentStageResponse>> updateStage(
            @PathVariable Long id,
            @Valid @RequestBody TreatmentStageUpdateRequest request) {
        TreatmentStageResponse updated = treatmentStageService.updateStage(id, request);
        return ResponseEntity.ok(ApiResponse.success("Treatment stage updated successfully", updated));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @Operation(summary = "Update treatment stage progress/status (ADMIN/DOCTOR)")
    public ResponseEntity<ApiResponse<TreatmentStageResponse>> updateStageStatus(
            @PathVariable Long id,
            @RequestParam TreatmentStageStatus status) {
        TreatmentStageResponse updated = treatmentStageService.updateStageStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Treatment stage status updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete treatment stage (ADMIN only)")
    public ResponseEntity<ApiResponse<Void>> deleteStage(@PathVariable Long id) {
        treatmentStageService.deleteStage(id);
        return ResponseEntity.ok(ApiResponse.success("Treatment stage deleted successfully", null));
    }
}
