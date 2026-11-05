package com.medical.management.treatmentroutes.service;

import com.medical.management.common.exception.ResourceNotFoundException;
import com.medical.management.common.response.PageResponse;
import com.medical.management.treatmentroutes.dto.TreatmentRouteCreateRequest;
import com.medical.management.treatmentroutes.dto.TreatmentRouteResponse;
import com.medical.management.treatmentroutes.dto.TreatmentRouteUpdateRequest;
import com.medical.management.treatmentroutes.entity.TreatmentRoute;
import com.medical.management.treatmentroutes.repository.TreatmentRouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TreatmentRouteServiceImpl implements TreatmentRouteService {

    private final TreatmentRouteRepository treatmentRouteRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<TreatmentRouteResponse> getRoutes(String keyword, Pageable pageable) {
        Page<TreatmentRoute> page = treatmentRouteRepository.searchRoutes(keyword, pageable);
        return PageResponse.from(page.map(TreatmentRouteResponse::fromEntity));
    }

    @Override
    @Transactional(readOnly = true)
    public TreatmentRouteResponse getRouteById(Long id) {
        TreatmentRoute route = treatmentRouteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TreatmentRoute", "id", id));
        return TreatmentRouteResponse.fromEntity(route);
    }

    @Override
    @Transactional
    public TreatmentRouteResponse createRoute(TreatmentRouteCreateRequest request) {
        TreatmentRoute route = TreatmentRoute.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .build();

        TreatmentRoute saved = treatmentRouteRepository.save(route);
        return TreatmentRouteResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public TreatmentRouteResponse updateRoute(Long id, TreatmentRouteUpdateRequest request) {
        TreatmentRoute route = treatmentRouteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TreatmentRoute", "id", id));

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            route.setTitle(request.getTitle());
        }

        if (request.getDescription() != null) {
            route.setDescription(request.getDescription());
        }

        TreatmentRoute updated = treatmentRouteRepository.save(route);
        return TreatmentRouteResponse.fromEntity(updated);
    }

    @Override
    @Transactional
    public void deleteRoute(Long id) {
        TreatmentRoute route = treatmentRouteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TreatmentRoute", "id", id));
        treatmentRouteRepository.delete(route);
    }
}
