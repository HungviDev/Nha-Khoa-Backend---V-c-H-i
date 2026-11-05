package com.medical.management.treatmentroutes.repository;

import com.medical.management.treatmentroutes.entity.TreatmentRoute;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TreatmentRouteRepository extends JpaRepository<TreatmentRoute, Long> {

    @Query("SELECT r FROM TreatmentRoute r WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<TreatmentRoute> searchRoutes(@Param("keyword") String keyword, Pageable pageable);
}
