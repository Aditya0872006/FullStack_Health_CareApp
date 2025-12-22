package com.example.HealthCareApp.appointment.dto;

import com.example.HealthCareApp.doctor.dto.DoctorDto;
import com.example.HealthCareApp.enums.AppintmentStatus;
import com.example.HealthCareApp.patient.dto.PatientDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppointmentDto {

    private Long id;

    @NotNull(message = "Doctor ID is required for booking.")
    private Long doctorId;

    private String purposeOfConsultation;

    private String initialSymptoms;

    @NotNull(message = "Start time is required for the appointment.")
    @Future(message = "Appointment must be scheduled for a future date and time.")
    private LocalDateTime startTime;

    private LocalDateTime endTime;
    private String meetingLink; // Unique link for the video/tele consultation

    private AppintmentStatus status;

    private DoctorDto doctor;
    private PatientDto patient;
}