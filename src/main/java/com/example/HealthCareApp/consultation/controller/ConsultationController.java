package com.example.HealthCareApp.consultation.controller;

import com.example.HealthCareApp.consultation.dto.ConsultationDto;
import com.example.HealthCareApp.consultation.service.ConsultationService;
import com.example.HealthCareApp.res.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/consultations")
public class ConsultationController {


    private final ConsultationService consultationService;

    @PostMapping
    @PreAuthorize("hasAuthority('DOCTOR')")
    public ResponseEntity<Response<ConsultationDto>> createConsultation(
            @RequestBody ConsultationDto consultationDTO) {
        return ResponseEntity.ok(consultationService.createConsultation(consultationDTO));
    }

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<Response<ConsultationDto>> getConsultationByAppointmentId(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(consultationService.getConsultationByAppointmentId(appointmentId));
    }

    @GetMapping("/history")
    public ResponseEntity<Response<List<ConsultationDto>>> getConsultationHistoryForPatient(
            @RequestParam(required = false) Long patientId) {
        return ResponseEntity.ok(consultationService.getConsultationHistoryForPatient(patientId));
    }

}