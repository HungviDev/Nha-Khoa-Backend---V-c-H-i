package com.medical.management.treatmentroutes.controller;

import com.medical.management.common.constants.AppConstants;
import com.medical.management.common.response.ApiResponse;
import com.medical.management.common.response.PageResponse;
import com.medical.management.treatmentroutes.dto.TreatmentRouteCreateRequest;
import com.medical.management.treatmentroutes.dto.TreatmentRouteResponse;
import com.medical.management.treatmentroutes.dto.TreatmentRouteUpdateRequest;
import com.medical.management.treatmentroutes.service.TreatmentRouteService;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/treatment-routes")
@RequiredArgsConstructor
@Tag(name = "Treatment Routes", description = "Treatment route management APIs")
public class TreatmentRouteController {

    private final TreatmentRouteService treatmentRouteService;

    @GetMapping
    @Operation(summary = "Get list of treatment routes with search and pagination")
    public ResponseEntity<ApiResponse<PageResponse<TreatmentRouteResponse>>> getRoutes(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_DIRECTION) String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        PageResponse<TreatmentRouteResponse> response = treatmentRouteService.getRoutes(keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get treatment route details by ID")
    public ResponseEntity<ApiResponse<TreatmentRouteResponse>> getRouteById(@PathVariable Long id) {
        TreatmentRouteResponse response = treatmentRouteService.getRouteById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @Operation(summary = "Create treatment route (ADMIN/DOCTOR)")
    public ResponseEntity<ApiResponse<TreatmentRouteResponse>> createRoute(
            @Valid @RequestBody TreatmentRouteCreateRequest request) {
        TreatmentRouteResponse created = treatmentRouteService.createRoute(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Treatment route created successfully", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @Operation(summary = "Update treatment route (ADMIN/DOCTOR)")
    public ResponseEntity<ApiResponse<TreatmentRouteResponse>> updateRoute(
            @PathVariable Long id,
            @Valid @RequestBody TreatmentRouteUpdateRequest request) {
        TreatmentRouteResponse updated = treatmentRouteService.updateRoute(id, request);
        return ResponseEntity.ok(ApiResponse.success("Treatment route updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete treatment route (ADMIN only)")
    public ResponseEntity<ApiResponse<Void>> deleteRoute(@PathVariable Long id) {
        treatmentRouteService.deleteRoute(id);
        return ResponseEntity.ok(ApiResponse.success("Treatment route deleted successfully", null));
    }
}
