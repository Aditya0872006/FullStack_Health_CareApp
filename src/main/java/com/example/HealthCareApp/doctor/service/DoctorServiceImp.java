package com.example.HealthCareApp.doctor.service;

import com.example.HealthCareApp.doctor.dto.DoctorDto;
import com.example.HealthCareApp.doctor.entity.Doctor;
import com.example.HealthCareApp.doctor.repository.DoctorRepo;
import com.example.HealthCareApp.enums.Specilization;
import com.example.HealthCareApp.exception.NotFoundExecption;
import com.example.HealthCareApp.res.Response;
import com.example.HealthCareApp.users.Entity.UserEntity;
import com.example.HealthCareApp.users.Service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorServiceImp implements DoctorService
{
    private final DoctorRepo doctorRepo;
    private final UserService userService;
    private final ModelMapper modelMapper;
    @Override
    public Response<DoctorDto> getDoctorProfile()
    {
        UserEntity user=userService.getCurrentUser();

        Doctor doctor = doctorRepo.findByUser(user).orElseThrow((()->new NotFoundExecption("not found")));

        return Response.<DoctorDto>builder()
                .statusCode(200)
                .message("Doctor profile retrieved successfully.")
                .data(modelMapper.map(doctor, DoctorDto.class))
                .build();
    }

    @Override
    public Response<?> updateDoctorProfile(DoctorDto doctorDTO) {

        // 1. Get current logged-in user
        UserEntity user = userService.getCurrentUser();

        // 2. Find doctor linked to this user
        Doctor doctor = doctorRepo.findByUser(user)
                .orElseThrow(() -> new NotFoundExecption("Doctor profile not found"));

        // 3. Update fields only if provided
        if (doctorDTO.getFirstName() != null) {
            doctor.setFirstName(doctorDTO.getFirstName());
        }

        if (doctorDTO.getLastName() != null) {
            doctor.setLastName(doctorDTO.getLastName());
        }

        if (doctorDTO.getSpecilization() != null) {
            doctor.setSpecilization(doctorDTO.getSpecilization());
        }

        if (doctorDTO.getLicenseNumber() != null) {
            doctor.setLicenseNumber(doctorDTO.getLicenseNumber());
        }

        // 4. Save updated doctor
        Doctor updatedDoctor = doctorRepo.save(doctor);

        // 5. Return response
        return Response.<DoctorDto>builder()
                .statusCode(200)
                .message("Doctor profile updated successfully.")
                .data(modelMapper.map(updatedDoctor, DoctorDto.class))
                .build();
    }


    @Override
    public Response<List<DoctorDto>> getAllDoctors()
    {
        List<Doctor> doctors=doctorRepo.findAll();
        List<DoctorDto> doctorDTOS = doctors.stream()
                .map(doctor -> modelMapper.map(doctor, DoctorDto.class))
                .toList();

        return Response.<List<DoctorDto>>builder()
                .statusCode(200)
                .message("All doctors retrieved successfully.")
                .data(doctorDTOS)
                .build();
    }

    @Override
    public Response<DoctorDto> getDoctorById(Long doctorId)
    {
        Doctor doctor = doctorRepo.findById(doctorId)
                .orElseThrow(() -> new NotFoundExecption("Doctor not found"));

        return Response.<DoctorDto>builder()
                .statusCode(200)
                .message("Doctor retrieved successfully.")
                .data(modelMapper.map(doctor, DoctorDto.class))
                .build();
    }

    @Override
    public Response<List<DoctorDto>> searchDoctorsBySpecialization(Specilization specialization) {
        List<Doctor> doctors = doctorRepo.findBySpecilization(specialization);

        List<DoctorDto> doctorDTOs = doctors.stream()
                .map(doctor -> modelMapper.map(doctor, DoctorDto.class))
                .toList();


        String message = doctors.isEmpty() ?
                "No doctors found for specialization: " + specialization.name() :
                "Doctors retrieved successfully for specialization: " + specialization.name();

        return Response.<List<DoctorDto>>builder()
                .statusCode(200)
                .message(message)
                .data(doctorDTOs)
                .build();

    }

    @Override
    public Response<List<Specilization>> getAllSpecializationEnums()
    {
        List<Specilization> specilizations= Arrays.asList(Specilization.values());
        return Response.<List<Specilization>>builder()
                .statusCode(200)
                .message("Specializations retrieved successfully")
                .data(specilizations)
                .build();
    }
}
