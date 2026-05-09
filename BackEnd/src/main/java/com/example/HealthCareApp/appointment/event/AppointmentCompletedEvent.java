package com.example.HealthCareApp.appointment.event;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AppointmentCompletedEvent {

    private final Long   appointmentId;
    private final String patientName;
    private final String doctorName;
    private final LocalDateTime completedAt;
}