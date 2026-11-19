package com.medical.management.doctors.repository;

import com.medical.management.doctors.entity.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    @EntityGraph(attributePaths = {"user", "user.role"})
    Optional<Doctor> findByUserId(Long userId);

    @EntityGraph(attributePaths = {"user", "user.role"})
    @Query("SELECT d FROM Doctor d WHERE d.id = :id")
    Optional<Doctor> findByIdWithUser(@Param("id") Long id);

    boolean existsByUserId(Long userId);

    @EntityGraph(attributePaths = {"user", "user.role"})
    @Query(value = "SELECT d FROM Doctor d WHERE d.user.role.id = 3 AND " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "LOWER(d.specialization) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(d.user.fullname) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(d.user.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "d.user.phone LIKE CONCAT('%', :keyword, '%'))",
           countQuery = "SELECT count(d) FROM Doctor d WHERE d.user.role.id = 3 AND " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "LOWER(d.specialization) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(d.user.fullname) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(d.user.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "d.user.phone LIKE CONCAT('%', :keyword, '%'))")
    Page<Doctor> searchDoctors(@Param("keyword") String keyword, Pageable pageable);
}
