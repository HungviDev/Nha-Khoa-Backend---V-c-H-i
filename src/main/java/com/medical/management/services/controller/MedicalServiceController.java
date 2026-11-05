package com.medical.management.services.controller;

import com.medical.management.common.constants.AppConstants;
import com.medical.management.common.response.ApiResponse;
import com.medical.management.common.response.PageResponse;
import com.medical.management.services.dto.MedicalServiceRequest;
import com.medical.management.services.dto.MedicalServiceResponse;
import com.medical.management.services.service.MedicalServiceService;
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
@RequestMapping("/api/v1/services")
@RequiredArgsConstructor
@Tag(name = "Services", description = "Medical service management APIs")
public class MedicalServiceController {

    private final MedicalServiceService medicalServiceService;

    @GetMapping
    @Operation(summary = "Get list of medical services with search and pagination")
    public ResponseEntity<ApiResponse<PageResponse<MedicalServiceResponse>>> getServices(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_DIRECTION) String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        PageResponse<MedicalServiceResponse> response = medicalServiceService.getServices(keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get medical service details by ID")
    public ResponseEntity<ApiResponse<MedicalServiceResponse>> getServiceById(@PathVariable Long id) {
        MedicalServiceResponse response = medicalServiceService.getServiceById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create medical service (ADMIN only)")
    public ResponseEntity<ApiResponse<MedicalServiceResponse>> createService(
            @Valid @RequestBody MedicalServiceRequest request) {
        MedicalServiceResponse created = medicalServiceService.createService(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Service created successfully", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update medical service (ADMIN only)")
    public ResponseEntity<ApiResponse<MedicalServiceResponse>> updateService(
            @PathVariable Long id,
            @Valid @RequestBody MedicalServiceRequest request) {
        MedicalServiceResponse updated = medicalServiceService.updateService(id, request);
        return ResponseEntity.ok(ApiResponse.success("Service updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete medical service (ADMIN only)")
    public ResponseEntity<ApiResponse<Void>> deleteService(@PathVariable Long id) {
        medicalServiceService.deleteService(id);
        return ResponseEntity.ok(ApiResponse.success("Service deleted successfully", null));
    }
}
