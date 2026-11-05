package com.medical.management.services.service;

import com.medical.management.common.response.PageResponse;
import com.medical.management.services.dto.MedicalServiceRequest;
import com.medical.management.services.dto.MedicalServiceResponse;
import org.springframework.data.domain.Pageable;

public interface MedicalServiceService {

    PageResponse<MedicalServiceResponse> getServices(String keyword, Pageable pageable);

    MedicalServiceResponse getServiceById(Long id);

    MedicalServiceResponse createService(MedicalServiceRequest request);

    MedicalServiceResponse updateService(Long id, MedicalServiceRequest request);

    void deleteService(Long id);
}
