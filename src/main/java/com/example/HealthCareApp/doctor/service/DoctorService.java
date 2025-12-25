package com.example.HealthCareApp.doctor.service;

import com.example.HealthCareApp.doctor.dto.DoctorDto;
import com.example.HealthCareApp.enums.Specilization;
import com.example.HealthCareApp.res.Response;

import java.util.List;

public interface DoctorService
{
    Response<DoctorDto> getDoctorProfile();
    Response<?>updateDoctorProfile(DoctorDto doctorDTO);
    Response<List<DoctorDto>> getAllDoctors();
    Response<DoctorDto> getDoctorById(Long doctorId);

    Response<List<DoctorDto>> searchDoctorsBySpecialization(Specilization specialization);
    Response<List<Specilization>> getAllSpecializationEnums();

}
