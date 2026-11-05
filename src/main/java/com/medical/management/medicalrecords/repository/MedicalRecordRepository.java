package com.medical.management.medicalrecords.repository;

import com.medical.management.medicalrecords.entity.MedicalRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {

    @EntityGraph(attributePaths = {"user", "doctor", "doctor.user", "service", "treatmentRoute"})
    @Query("SELECT r FROM MedicalRecord r WHERE r.id = :id")
    Optional<MedicalRecord> findByIdWithDetails(@Param("id") Long id);

    @EntityGraph(attributePaths = {"user", "doctor", "doctor.user", "service", "treatmentRoute"})
    Page<MedicalRecord> findByUserId(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "doctor", "doctor.user", "service", "treatmentRoute"})
    @Query("SELECT r FROM MedicalRecord r WHERE " +
           "(:userId IS NULL OR r.user.id = :userId) AND " +
           "(:doctorId IS NULL OR r.doctor.id = :doctorId) AND " +
           "(:keyword IS NULL OR :keyword = '' OR LOWER(r.diagnosis) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<MedicalRecord> searchMedicalRecords(
            @Param("userId") Long userId,
            @Param("doctorId") Long doctorId,
            @Param("keyword") String keyword,
            Pageable pageable);
}
