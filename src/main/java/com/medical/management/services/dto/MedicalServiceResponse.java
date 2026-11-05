package com.medical.management.services.dto;

import com.medical.management.services.entity.MedicalService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalServiceResponse {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String image;

    public static MedicalServiceResponse fromEntity(MedicalService service) {
        if (service == null) {
            return null;
        }
        return MedicalServiceResponse.builder()
                .id(service.getId())
                .name(service.getName())
                .description(service.getDescription())
                .price(service.getPrice())
                .image(service.getImage())
                .build();
    }
}
