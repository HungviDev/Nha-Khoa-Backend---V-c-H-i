package com.medical.management.appointments.service;

import com.medical.management.appointments.dto.AppointmentCreateRequest;
import com.medical.management.appointments.dto.AppointmentResponse;
import com.medical.management.appointments.dto.AppointmentStatusUpdateRequest;
import com.medical.management.appointments.dto.AppointmentUpdateRequest;
import com.medical.management.appointments.entity.AppointmentStatus;
import com.medical.management.common.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface AppointmentService {

    PageResponse<AppointmentResponse> getAppointments(Long userId, Long doctorId, AppointmentStatus status, Pageable pageable);

    AppointmentResponse getAppointmentById(Long id);

    AppointmentResponse createAppointment(AppointmentCreateRequest request);

    AppointmentResponse updateAppointment(Long id, AppointmentUpdateRequest request);

    AppointmentResponse updateAppointmentStatus(Long id, AppointmentStatusUpdateRequest request);

    void cancelAppointment(Long id, String reason);
}
