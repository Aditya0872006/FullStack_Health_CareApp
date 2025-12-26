package com.example.HealthCareApp.patient.service;

import com.example.HealthCareApp.enums.BloodGroup;
import com.example.HealthCareApp.enums.Genotype;
import com.example.HealthCareApp.exception.BadRequestException;
import com.example.HealthCareApp.exception.NotFoundExecption;
import com.example.HealthCareApp.patient.dto.PatientDto;
import com.example.HealthCareApp.patient.entity.Patient;
import com.example.HealthCareApp.patient.repository.PatientRepo;
import com.example.HealthCareApp.res.Response;
import com.example.HealthCareApp.users.Entity.UserEntity;
import com.example.HealthCareApp.users.Service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientServiceImp implements PatientService
{
    private final PatientRepo patientRepo;
    private final UserService userService;
    private final ModelMapper modelMapper;
    @Override
    public Response<PatientDto> getPatientProfile()
    {
        UserEntity user = userService.getCurrentUser();
        Patient patient=patientRepo.findByUser(user).orElseThrow(()->new NotFoundExecption("not found"));


        return Response.<PatientDto>builder()
                .statusCode(200)
                .message("Patient profile retrieved successfully.")
                .data(modelMapper.map(patient, PatientDto.class))
                .build();
    }

    @Override
    public Response<?> updatePatientProfile(PatientDto patientDTO) {


        // 1. Get current logged-in user
        UserEntity user = userService.getCurrentUser();

        // 2. Find patient linked to this user
        Patient patient = patientRepo.findByUser(user)
                .orElseThrow(() -> new NotFoundExecption("Patient not found"));

        // 3. Update fields only if they are not null
        if (patientDTO.getFirstName() != null) {
            patient.setFirstName(patientDTO.getFirstName());
        }

        if (patientDTO.getLastName() != null) {
            patient.setLastName(patientDTO.getLastName());
        }

        if (patientDTO.getDateOfBirth() != null) {
            patient.setDateOfBirth(patientDTO.getDateOfBirth());
        }

        if (patientDTO.getPhone() != null) {
            patient.setPhone(patientDTO.getPhone());
        }

        if (patientDTO.getKnownAllergies() != null) {
            patient.setKnownAllergies(patientDTO.getKnownAllergies());
        }

        if (patientDTO.getBloodGroup() != null) {
            patient.setBloodGroup(patientDTO.getBloodGroup());
        }

        if (patientDTO.getGenotype() != null) {
            patient.setGenotype(patientDTO.getGenotype());
        }

        // 4. Save updated patient
        Patient updatedPatient = patientRepo.save(patient);

        // 5. Return response
        return Response.<PatientDto>builder()
                .statusCode(200)
                .message("Patient profile updated successfully.")
                .data(modelMapper.map(updatedPatient, PatientDto.class))
                .build();
    }


    @Override
    public Response<PatientDto> getPatientById(Long patientId)
    {
        UserEntity user=userService.getCurrentUser();
        Patient patient = patientRepo.findById(patientId)
                .orElseThrow(() -> new NotFoundExecption("Patient not found"));
        if(!patient.getUser().getId().equals(user.getId()))
        {
            throw new BadRequestException("you dont have access");
        }


        PatientDto patientDTO = modelMapper.map(patient, PatientDto.class);

        return Response.<PatientDto>builder()
                .statusCode(200)
                .message("Patient retrieved successfully.")
                .data(patientDTO)
                .build();
    }

    @Override
    public Response<List<BloodGroup>> getAllBloodGroupEnums()
    {
        List<BloodGroup> bloodGroups = Arrays.asList(BloodGroup.values());

        return Response.<List<BloodGroup>>builder()
                .statusCode(200)
                .message("BloodGroups retrieved successfully")
                .data(bloodGroups)
                .build();
    }

    @Override
    public Response<List<Genotype>> getAllGenotypeEnums() {
        List<Genotype> genotypes = Arrays.asList(Genotype.values());

        return Response.<List<Genotype>>builder()
                .statusCode(200)
                .message("Genotypes retrieved successfully")
                .data(genotypes)
                .build();
    }
}
