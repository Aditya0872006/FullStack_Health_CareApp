package com.example.HealthCareApp.patient.controller;

import com.example.HealthCareApp.enums.BloodGroup;
import com.example.HealthCareApp.enums.Genotype;
import com.example.HealthCareApp.patient.dto.PatientDto;
import com.example.HealthCareApp.patient.service.PatientService;
import com.example.HealthCareApp.res.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    @GetMapping("/me")
    public ResponseEntity<Response<PatientDto>> getPatientProfile() {
        return ResponseEntity.ok(patientService.getPatientProfile());
    }

    @PutMapping("/me")
    public ResponseEntity<Response<?>> updatePatientProfile(@RequestBody PatientDto patientDTO) {
        return ResponseEntity.ok(patientService.updatePatientProfile(patientDTO));
    }


    @GetMapping("/{patientId}")
    public ResponseEntity<Response<PatientDto>> getPatientById(@PathVariable Long patientId) {
        return ResponseEntity.ok(patientService.getPatientById(patientId));
    }


    @GetMapping("/bloodgroup")
    public ResponseEntity<Response<List<BloodGroup>>> getAllBloodGroupEnums() {
        return ResponseEntity.ok(patientService.getAllBloodGroupEnums());
    }

    @GetMapping("/genotype")
    public ResponseEntity<Response<List<Genotype>>> getAllGenotypeEnums() {
        return ResponseEntity.ok(patientService.getAllGenotypeEnums());
    }


}