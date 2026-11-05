package com.medical.management.treatmentroutes.service;

import com.medical.management.common.response.PageResponse;
import com.medical.management.treatmentroutes.dto.TreatmentRouteCreateRequest;
import com.medical.management.treatmentroutes.dto.TreatmentRouteResponse;
import com.medical.management.treatmentroutes.dto.TreatmentRouteUpdateRequest;
import org.springframework.data.domain.Pageable;

public interface TreatmentRouteService {

    PageResponse<TreatmentRouteResponse> getRoutes(String keyword, Pageable pageable);

    TreatmentRouteResponse getRouteById(Long id);

    TreatmentRouteResponse createRoute(TreatmentRouteCreateRequest request);

    TreatmentRouteResponse updateRoute(Long id, TreatmentRouteUpdateRequest request);

    void deleteRoute(Long id);
}
