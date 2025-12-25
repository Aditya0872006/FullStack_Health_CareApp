package com.example.HealthCareApp.users.Service;

import com.example.HealthCareApp.doctor.dto.DoctorDto;
import com.example.HealthCareApp.doctor.repository.DoctorRepo;
import com.example.HealthCareApp.notification.service.NotificationService;
import com.example.HealthCareApp.patient.repository.PatientRepo;
import com.example.HealthCareApp.res.Response;
import com.example.HealthCareApp.role.Repository.RoleRepo;
import com.example.HealthCareApp.security.JwtService;
import com.example.HealthCareApp.users.Dto.LoginRequest;
import com.example.HealthCareApp.users.Dto.LoginResponse;
import com.example.HealthCareApp.users.Dto.RegistrationRequest;
import com.example.HealthCareApp.users.Dto.ResetPasswordRequest;
import com.example.HealthCareApp.users.Repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImp implements AuthService
{

    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final NotificationService notificationService;

    private final PatientRepo patientRepo;
    private final DoctorRepo doctorRepo;
    
    @Override
    public Response<String> registerUser(RegistrationRequest registrationRequest)
    {
        return null;
    }

    @Override
    public Response<LoginResponse> login(LoginRequest loginRequest) {
        return null;
    }

    @Override
    public Response<?> forgetPassword(String email) {
        return null;
    }

    @Override
    public Response<?> resetPasswordViaCode(ResetPasswordRequest resetPasswordRequest) {
        return null;
    }
}
