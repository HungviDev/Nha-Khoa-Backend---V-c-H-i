package com.medical.management.appointments.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum AppointmentStatus {
    PENDING,
    CONFIRMED,
    COMPLETED,
    CANCELLED;

    @JsonCreator
    public static AppointmentStatus fromString(String value) {
        if (value == null) {
            return null;
        }
        for (AppointmentStatus status : AppointmentStatus.values()) {
            if (status.name().equalsIgnoreCase(value.trim())) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown appointment status: " + value);
    }
}
