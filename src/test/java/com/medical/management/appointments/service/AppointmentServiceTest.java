package com.medical.management.appointments.service;

import com.medical.management.appointments.dto.AppointmentCreateRequest;
import com.medical.management.appointments.dto.AppointmentResponse;
import com.medical.management.appointments.entity.Appointment;
import com.medical.management.appointments.entity.AppointmentStatus;
import com.medical.management.appointments.repository.AppointmentRepository;
import com.medical.management.common.exception.BadRequestException;
import com.medical.management.doctors.entity.Doctor;
import com.medical.management.doctors.repository.DoctorRepository;
import com.medical.management.services.entity.MedicalService;
import com.medical.management.services.repository.MedicalServiceRepository;
import com.medical.management.users.entity.User;
import com.medical.management.users.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private MedicalServiceRepository medicalServiceRepository;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    private User patient;
    private Doctor doctor;
    private MedicalService medicalService;
    private LocalDateTime futureDate;

    @BeforeEach
    void setUp() {
        futureDate = LocalDateTime.now().plusDays(2).withHour(10).withMinute(0).withSecond(0).withNano(0);

        patient = User.builder()
                .id(1L)
                .fullname("Patient A")
                .email("patient@gmail.com")
                .phone("0987654321")
                .build();

        doctor = Doctor.builder()
                .id(1L)
                .specialization("Nha khoa tong quat")
                .experienceYears(10)
                .user(User.builder().id(2L).fullname("Dr. Minh").build())
                .build();

        medicalService = MedicalService.builder()
                .id(1L)
                .name("Kham rang tong quat")
                .price(BigDecimal.valueOf(200000))
                .build();
    }

    @Test
    @DisplayName("Should successfully create appointment when no collision exists")
    void createAppointment_Success() {
        AppointmentCreateRequest request = AppointmentCreateRequest.builder()
                .userId(1L)
                .doctorId(1L)
                .serviceId(1L)
                .appointmentDate(futureDate)
                .note("Dau rang ham duoi")
                .build();

        when(appointmentRepository.existsConflictingDoctorAppointment(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class), eq(null)))
                .thenReturn(false);
        when(appointmentRepository.existsConflictingUserAppointment(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class), eq(null)))
                .thenReturn(false);
        when(userRepository.findByIdWithRole(1L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findByIdWithUser(1L)).thenReturn(Optional.of(doctor));
        when(medicalServiceRepository.findById(1L)).thenReturn(Optional.of(medicalService));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(inv -> {
            Appointment a = inv.getArgument(0);
            a.setId(10L);
            a.setCreatedAt(LocalDateTime.now());
            return a;
        });

        AppointmentResponse response = appointmentService.createAppointment(request);

        assertNotNull(response);
        assertEquals(AppointmentStatus.PENDING, response.getStatus());
        assertEquals(1L, response.getUserId());
        assertEquals(1L, response.getDoctorId());
        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    @DisplayName("Should reject appointment if doctor is already booked at that time")
    void createAppointment_DoctorConflict_ThrowsException() {
        AppointmentCreateRequest request = AppointmentCreateRequest.builder()
                .userId(1L)
                .doctorId(1L)
                .serviceId(1L)
                .appointmentDate(futureDate)
                .build();

        when(appointmentRepository.existsConflictingDoctorAppointment(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class), eq(null)))
                .thenReturn(true);

        assertThrows(BadRequestException.class, () -> appointmentService.createAppointment(request));
    }

    @Test
    @DisplayName("Should reject appointment if patient already has overlapping appointment")
    void createAppointment_UserConflict_ThrowsException() {
        AppointmentCreateRequest request = AppointmentCreateRequest.builder()
                .userId(1L)
                .doctorId(1L)
                .serviceId(1L)
                .appointmentDate(futureDate)
                .build();

        when(appointmentRepository.existsConflictingDoctorAppointment(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class), eq(null)))
                .thenReturn(false);
        when(appointmentRepository.existsConflictingUserAppointment(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class), eq(null)))
                .thenReturn(true);

        assertThrows(BadRequestException.class, () -> appointmentService.createAppointment(request));
    }

    @Test
    @DisplayName("Should cancel appointment successfully")
    void cancelAppointment_Success() {
        Appointment appointment = Appointment.builder()
                .id(5L)
                .user(patient)
                .doctor(doctor)
                .service(medicalService)
                .status(AppointmentStatus.PENDING)
                .appointmentDate(futureDate)
                .build();

        when(appointmentRepository.findByIdWithDetails(5L)).thenReturn(Optional.of(appointment));

        appointmentService.cancelAppointment(5L, "Ban viec dot xuat");

        assertEquals(AppointmentStatus.CANCELLED, appointment.getStatus());
        verify(appointmentRepository).save(appointment);
    }

    @Test
    @DisplayName("Should not allow cancellation of completed appointment")
    void cancelAppointment_AlreadyCompleted_ThrowsException() {
        Appointment appointment = Appointment.builder()
                .id(5L)
                .user(patient)
                .doctor(doctor)
                .service(medicalService)
                .status(AppointmentStatus.COMPLETED)
                .appointmentDate(futureDate)
                .build();

        when(appointmentRepository.findByIdWithDetails(5L)).thenReturn(Optional.of(appointment));

        assertThrows(BadRequestException.class, () -> appointmentService.cancelAppointment(5L, "Reason"));
    }
}
