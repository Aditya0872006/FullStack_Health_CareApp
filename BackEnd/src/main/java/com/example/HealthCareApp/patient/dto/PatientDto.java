package com.example.HealthCareApp.patient.dto;
import com.example.HealthCareApp.enums.BloodGroup;
import com.example.HealthCareApp.enums.Genotype;
import com.example.HealthCareApp.users.Dto.UserDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PatientDto
{

    private Long id;

    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String phone;

    private String knownAllergies;

    private BloodGroup bloodGroup;

    private Genotype genotype;

    private UserDto user;
}