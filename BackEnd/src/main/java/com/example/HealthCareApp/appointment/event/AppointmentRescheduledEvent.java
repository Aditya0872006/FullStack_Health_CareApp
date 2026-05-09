package com.example.HealthCareApp.appointment.event;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AppointmentRescheduledEvent {

    private final Long   appointmentId;
    private final String patientName;
    private final String patientEmail;
    private final String doctorName;
    private final String doctorEmail;
    private final LocalDateTime newAppointmentTime;
    private final String meetingLink;
    private final String purposeOfConsultation;
}