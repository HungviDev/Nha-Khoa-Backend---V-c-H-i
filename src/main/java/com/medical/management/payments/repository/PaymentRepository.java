package com.medical.management.payments.repository;

import com.medical.management.payments.entity.Payment;
import com.medical.management.payments.entity.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @EntityGraph(attributePaths = {"user", "user.role", "treatmentStage", "treatmentStage.treatmentRoute"})
    @Query("SELECT p FROM Payment p WHERE p.id = :id")
    Optional<Payment> findByIdWithDetails(@Param("id") Long id);

    @EntityGraph(attributePaths = {"user", "user.role", "treatmentStage", "treatmentStage.treatmentRoute"})
    Page<Payment> findByUserId(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "user.role", "treatmentStage", "treatmentStage.treatmentRoute"})
    @Query("SELECT p FROM Payment p WHERE " +
           "(:userId IS NULL OR p.user.id = :userId) AND " +
           "(:status IS NULL OR p.status = :status)")
    Page<Payment> searchPayments(
            @Param("userId") Long userId,
            @Param("status") PaymentStatus status,
            Pageable pageable);
}
