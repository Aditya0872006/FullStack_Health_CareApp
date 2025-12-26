package com.example.HealthCareApp.doctor.dto;

import com.example.HealthCareApp.enums.Specilization;
import com.example.HealthCareApp.users.Dto.UserDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DoctorDto {


    private Long id;

    private String firstName;
    private String lastName;

    private Specilization specilization;

    private String licenseNumber;

    private UserDto user;


}