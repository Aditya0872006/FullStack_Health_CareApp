package com.example.HealthCareApp.patient.service;

import com.example.HealthCareApp.enums.BloodGroup;
import com.example.HealthCareApp.enums.Genotype;
import com.example.HealthCareApp.patient.dto.PatientDto;
import com.example.HealthCareApp.res.Response;

import java.util.List;

public interface PatientService {


    Response<PatientDto> getPatientProfile();

    Response<?> updatePatientProfile(PatientDto patientDTO);

    Response<PatientDto> getPatientById(Long patientId);

    Response<List<BloodGroup>> getAllBloodGroupEnums();
    Response<List<Genotype>>getAllGenotypeEnums();

}