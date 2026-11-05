package com.medical.management.services.repository;

import com.medical.management.services.entity.MedicalService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicalServiceRepository extends JpaRepository<MedicalService, Long> {

    @Query("SELECT s FROM MedicalService s WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<MedicalService> searchServices(@Param("keyword") String keyword, Pageable pageable);
}
