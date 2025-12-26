package com.example.HealthCareApp.users.Dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class ResetPasswordRequest {

    //will be used to request for forgot password
    private String email;

    //will be used to set new password
    private String code;
    private String newPassword;
}