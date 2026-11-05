package com.medical.management.treatmentstages.repository;

import com.medical.management.treatmentstages.entity.TreatmentStage;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TreatmentStageRepository extends JpaRepository<TreatmentStage, Long> {

    @EntityGraph(attributePaths = {"treatmentRoute"})
    List<TreatmentStage> findByTreatmentRouteIdOrderBySequenceOrderAsc(Long treatmentRouteId);

    @EntityGraph(attributePaths = {"treatmentRoute"})
    @Query("SELECT s FROM TreatmentStage s WHERE s.id = :id")
    Optional<TreatmentStage> findByIdWithRoute(@Param("id") Long id);
}
