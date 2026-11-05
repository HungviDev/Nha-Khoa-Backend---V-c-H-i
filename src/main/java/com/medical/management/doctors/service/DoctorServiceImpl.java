package com.medical.management.doctors.service;

import com.medical.management.common.exception.BadRequestException;
import com.medical.management.common.exception.ResourceNotFoundException;
import com.medical.management.common.response.PageResponse;
import com.medical.management.doctors.dto.DoctorCreateRequest;
import com.medical.management.doctors.dto.DoctorResponse;
import com.medical.management.doctors.dto.DoctorUpdateRequest;
import com.medical.management.doctors.entity.Doctor;
import com.medical.management.doctors.repository.DoctorRepository;
import com.medical.management.users.entity.User;
import com.medical.management.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DoctorResponse> getDoctors(String keyword, Pageable pageable) {
        Page<Doctor> page = doctorRepository.searchDoctors(keyword, pageable);
        return PageResponse.from(page.map(DoctorResponse::fromEntity));
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorResponse getDoctorById(Long id) {
        Doctor doctor = doctorRepository.findByIdWithUser(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", id));
        return DoctorResponse.fromEntity(doctor);
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorResponse getDoctorByUserId(Long userId) {
        Doctor doctor = doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "userId", userId));
        return DoctorResponse.fromEntity(doctor);
    }

    @Override
    @Transactional
    public DoctorResponse createDoctor(DoctorCreateRequest request) {
        if (doctorRepository.existsByUserId(request.getUserId())) {
            throw new BadRequestException("Doctor profile already exists for user ID: " + request.getUserId());
        }

        User user = userRepository.findByIdWithRole(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));

        Doctor doctor = Doctor.builder()
                .user(user)
                .specialization(request.getSpecialization())
                .experienceYears(request.getExperienceYears())
                .build();

        Doctor saved = doctorRepository.save(doctor);
        return DoctorResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public DoctorResponse updateDoctor(Long id, DoctorUpdateRequest request) {
        Doctor doctor = doctorRepository.findByIdWithUser(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", id));

        if (request.getSpecialization() != null && !request.getSpecialization().isBlank()) {
            doctor.setSpecialization(request.getSpecialization());
        }

        if (request.getExperienceYears() != null) {
            doctor.setExperienceYears(request.getExperienceYears());
        }

        Doctor updated = doctorRepository.save(doctor);
        return DoctorResponse.fromEntity(updated);
    }

    @Override
    @Transactional
    public void deleteDoctor(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", id));
        doctorRepository.delete(doctor);
    }
}
