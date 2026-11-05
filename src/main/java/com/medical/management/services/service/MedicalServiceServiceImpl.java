package com.medical.management.services.service;

import com.medical.management.common.exception.ResourceNotFoundException;
import com.medical.management.common.response.PageResponse;
import com.medical.management.services.dto.MedicalServiceRequest;
import com.medical.management.services.dto.MedicalServiceResponse;
import com.medical.management.services.entity.MedicalService;
import com.medical.management.services.repository.MedicalServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MedicalServiceServiceImpl implements MedicalServiceService {

    private final MedicalServiceRepository medicalServiceRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MedicalServiceResponse> getServices(String keyword, Pageable pageable) {
        Page<MedicalService> page = medicalServiceRepository.searchServices(keyword, pageable);
        return PageResponse.from(page.map(MedicalServiceResponse::fromEntity));
    }

    @Override
    @Transactional(readOnly = true)
    public MedicalServiceResponse getServiceById(Long id) {
        MedicalService service = medicalServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service", "id", id));
        return MedicalServiceResponse.fromEntity(service);
    }

    @Override
    @Transactional
    public MedicalServiceResponse createService(MedicalServiceRequest request) {
        MedicalService service = MedicalService.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .image(request.getImage())
                .build();

        MedicalService saved = medicalServiceRepository.save(service);
        return MedicalServiceResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public MedicalServiceResponse updateService(Long id, MedicalServiceRequest request) {
        MedicalService service = medicalServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service", "id", id));

        service.setName(request.getName());
        service.setDescription(request.getDescription());
        service.setPrice(request.getPrice());
        service.setImage(request.getImage());

        MedicalService updated = medicalServiceRepository.save(service);
        return MedicalServiceResponse.fromEntity(updated);
    }

    @Override
    @Transactional
    public void deleteService(Long id) {
        MedicalService service = medicalServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service", "id", id));
        medicalServiceRepository.delete(service);
    }
}
