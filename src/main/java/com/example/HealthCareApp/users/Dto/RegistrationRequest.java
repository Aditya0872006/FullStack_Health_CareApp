package com.example.HealthCareApp.users.Dto;

import com.example.HealthCareApp.enums.Specilization;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegistrationRequest {


    @NotBlank(message = "Name is required")
    private String name;

    @Enumerated(EnumType.STRING)
    private Specilization specialization;

    private String licenseNumber;

    @NotBlank(message = "Email is required")
    @Email
    private String email;

    private List<String> roles;

    @NotBlank(message = "Password is required")
    private String password;
}