package com.medical.management.appointments.repository;

import com.medical.management.appointments.entity.Appointment;
import com.medical.management.appointments.entity.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @EntityGraph(attributePaths = {"user", "doctor", "doctor.user", "service"})
    @Query("SELECT a FROM Appointment a WHERE a.id = :id")
    Optional<Appointment> findByIdWithDetails(@Param("id") Long id);

    @EntityGraph(attributePaths = {"user", "doctor", "doctor.user", "service"})
    Page<Appointment> findByUserId(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "doctor", "doctor.user", "service"})
    Page<Appointment> findByDoctorId(Long doctorId, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "doctor", "doctor.user", "service"})
    @Query("SELECT a FROM Appointment a WHERE " +
           "(:userId IS NULL OR a.user.id = :userId) AND " +
           "(:doctorId IS NULL OR a.doctor.id = :doctorId) AND " +
           "(:status IS NULL OR a.status = :status)")
    Page<Appointment> searchAppointments(
            @Param("userId") Long userId,
            @Param("doctorId") Long doctorId,
            @Param("status") AppointmentStatus status,
            Pageable pageable);

    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE " +
           "a.doctor.id = :doctorId AND " +
           "a.status NOT IN ('CANCELLED') AND " +
           "a.appointmentDate BETWEEN :start AND :end AND " +
           "(:excludeId IS NULL OR a.id != :excludeId)")
    boolean existsConflictingDoctorAppointment(
            @Param("doctorId") Long doctorId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("excludeId") Long excludeId);

    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE " +
           "a.user.id = :userId AND " +
           "a.status NOT IN ('CANCELLED') AND " +
           "a.appointmentDate BETWEEN :start AND :end AND " +
           "(:excludeId IS NULL OR a.id != :excludeId)")
    boolean existsConflictingUserAppointment(
            @Param("userId") Long userId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("excludeId") Long excludeId);
}
