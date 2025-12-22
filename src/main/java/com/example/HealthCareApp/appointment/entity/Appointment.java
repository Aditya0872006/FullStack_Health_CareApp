package com.example.HealthCareApp.appointment.entity;

import com.example.HealthCareApp.doctor.entity.Doctor;
import com.example.HealthCareApp.enums.AppintmentStatus;
import com.example.HealthCareApp.patient.entity.Patient;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String meetingLink;

    private String purposeOfConsultation;

    private String initialSymptoms;

    @Enumerated(EnumType.STRING)
    private AppintmentStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;


}