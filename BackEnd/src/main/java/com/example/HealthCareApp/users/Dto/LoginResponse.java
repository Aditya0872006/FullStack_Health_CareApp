package com.example.HealthCareApp.users.Dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@Builder
public class LoginResponse {

    private String token;
    private Set<String> roles;
}