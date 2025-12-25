package com.example.HealthCareApp.users.Service;

import com.example.HealthCareApp.res.Response;
import com.example.HealthCareApp.users.Dto.LoginRequest;
import com.example.HealthCareApp.users.Dto.LoginResponse;
import com.example.HealthCareApp.users.Dto.RegistrationRequest;
import com.example.HealthCareApp.users.Dto.ResetPasswordRequest;

public interface AuthService
{
    Response<String> registerUser(RegistrationRequest registrationRequest);

    Response<LoginResponse> login(LoginRequest loginRequest);

    Response<?> forgetPassword(String email);

    Response<?> resetPasswordViaCode(ResetPasswordRequest resetPasswordRequest);
}
