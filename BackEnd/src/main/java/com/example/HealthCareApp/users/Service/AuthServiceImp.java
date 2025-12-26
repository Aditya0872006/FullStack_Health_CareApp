package com.example.HealthCareApp.users.Service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.example.HealthCareApp.doctor.dto.DoctorDto;
import com.example.HealthCareApp.doctor.entity.Doctor;
import com.example.HealthCareApp.doctor.repository.DoctorRepo;
import com.example.HealthCareApp.exception.BadRequestException;
import com.example.HealthCareApp.exception.NotFoundExecption;
import com.example.HealthCareApp.notification.dto.NotificationDto;
import com.example.HealthCareApp.notification.service.NotificationService;
import com.example.HealthCareApp.patient.entity.Patient;
import com.example.HealthCareApp.patient.repository.PatientRepo;
import com.example.HealthCareApp.res.Response;
import com.example.HealthCareApp.role.Entity.RoleEntity;
import com.example.HealthCareApp.role.Repository.RoleRepo;
import com.example.HealthCareApp.security.JwtService;
import com.example.HealthCareApp.users.Dto.LoginRequest;
import com.example.HealthCareApp.users.Dto.LoginResponse;
import com.example.HealthCareApp.users.Dto.RegistrationRequest;
import com.example.HealthCareApp.users.Dto.ResetPasswordRequest;
import com.example.HealthCareApp.users.Entity.PasswordResetCode;
import com.example.HealthCareApp.users.Entity.UserEntity;
import com.example.HealthCareApp.users.Repository.PasswordResetRepo;
import com.example.HealthCareApp.users.Repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

    private final PasswordResetRepo passwordResetRepo;
    private final CodeGenerator codeGenerator;


    @Value("${login.link}")
    private String loginLink;

    @Value("${password.reset.link}")
    private String resetLink;

    @Override
    public Response<String> registerUser(RegistrationRequest registrationRequest)
    {
        // 1. Check if user already exists
        if(userRepo.existsByEmail(registrationRequest.getEmail())) {
            throw new BadRequestException("User with email already exists");
        }

        //2. Get requested roles or default to PATIENT
        Set<String> roleNames = (registrationRequest.getRoles() == null || registrationRequest.getRoles().isEmpty())
                ? new HashSet<>(Set.of("PATIENT"))
                : registrationRequest.getRoles().stream()
                .map(String::toUpperCase)
                .collect(Collectors.toSet());

        // 3. Validate doctor registration
        if (roleNames.contains("DOCTOR") &&
                (registrationRequest.getLicenseNumber() == null || registrationRequest.getLicenseNumber().isBlank())) {
            throw new BadRequestException("License number is required for doctor registration");
        }

        //cheack all role exixts
        Set<RoleEntity> roles = new HashSet<>();

        for (String roleName : roleNames)
        {
            Optional<RoleEntity> roleOpt = roleRepo.findByName(roleName);
            if (roleOpt.isPresent()) {
                roles.add(roleOpt.get());  // add the actual RoleEntity
            }
        }

        if (roles.size() != roleNames.size()) {
            throw new NotFoundException("One or more roles not found in database");
        }


        // 5. Create and save user
        UserEntity user = UserEntity.builder()
                .email(registrationRequest.getEmail())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .name(registrationRequest.getName())
                .roles(roles)
                .build();

        UserEntity savedUser = userRepo.save(user);

        // 6. Create profile based on role
        for (RoleEntity role : roles) {
            if ("PATIENT".equals(role.getName())) {
                createPatientProfile(savedUser);
            } else if ("DOCTOR".equals(role.getName())) {
                createDoctorProfile(registrationRequest, savedUser);
            }
        }

        // 7. Send welcome email
        sendRegistrationEmail(registrationRequest, savedUser);

        // 8. Return response
        return Response.<String>builder()
                .statusCode(200)
                .message("Registration successful. Welcome email sent.")
                .data(savedUser.getEmail())
                .build();


    }


    @Override
    public Response<LoginResponse> login(LoginRequest loginRequest)
    {
        String email=loginRequest.getEmail();
        String password=loginRequest.getPassword();

        UserEntity user=userRepo.findByEmail(email).orElseThrow(()->new NotFoundExecption("user Not Found"));

        if(!passwordEncoder.matches(password,user.getPassword()))
        {
            throw new BadRequestException("wrong password");
        }
        String token = jwtService.generateToken(user.getEmail());

        LoginResponse loginResponse = LoginResponse.builder()
                .roles(
                        user.getRoles()
                                .stream()
                                .map(RoleEntity::getName)
                                .collect(Collectors.toSet())
                )
                .token(token)
                .build();



        return Response.<LoginResponse>builder()
                .statusCode(200)
                .message("Login Successful")
                .data(loginResponse)
                .build();

    }

    @Override
    public Response<?> forgetPassword(String email)
    {
        UserEntity user = userRepo.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User Not Found"));

        passwordResetRepo.deleteByUserId(user.getId());

        String code = codeGenerator.generateUniqueCode();

        PasswordResetCode resetCode = PasswordResetCode.builder()
                .user(user)
                .code(code)
                .expiryDate(calculateExpiryDate())
                .used(false)
                .build();

        passwordResetRepo.save(resetCode);

        //send email reset link out
        NotificationDto passwordResetEmail = NotificationDto.builder()
                .recipient(user.getEmail())
                .subject("Password Reset Code")
                .templateName("password-reset")
                .templateVariables(Map.of( // Using Map.of() for concise, immutable map creation
                        "name", user.getName(),
                        "resetLink", resetLink + code
                ))
                .build();

        notificationService.sendMail(passwordResetEmail, user);

        return Response.builder()
                .statusCode(200)
                .message("Password reset code sent to your email")
                .build();
    }

    @Override
    public Response<?> resetPasswordViaCode(ResetPasswordRequest resetPasswordRequest)
    {
        String code = resetPasswordRequest.getCode();
        String newPassword = resetPasswordRequest.getNewPassword();




        // Find and validate code
        PasswordResetCode resetCode = passwordResetRepo.findByCode(code)
                .orElseThrow(() -> new BadRequestException("Invalid reset code"));

        // Check expiration first
        if (resetCode.getExpiryDate().isBefore(LocalDateTime.now())) {
            passwordResetRepo.delete(resetCode); // Clean up expired code
            throw new BadRequestException("Reset code has expired");
        }

        //update the password
        UserEntity user = resetCode.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);

        // Delete the code immediately after successful use
        passwordResetRepo.delete(resetCode);


        // Send password confirmation email
        NotificationDto passwordResetEmail = NotificationDto.builder()
                .recipient(user.getEmail())
                .subject("Password Updated Successfully")
                .templateName("password-update-confirmation")
                .templateVariables(Map.of(
                        "name", user.getName()
                ))
                .build();

        notificationService.sendMail(passwordResetEmail, user);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Password updated successfully")
                .build();

    }

    private void createPatientProfile(UserEntity user)
    {
        Patient patient = Patient.builder()
                .user(user)
                .build();
        patientRepo.save(patient);
    }

    private void createDoctorProfile(RegistrationRequest request,UserEntity user)
    {
        Doctor doctor = Doctor.builder()
                .specilization(request.getSpecialization())
                .licenseNumber(request.getLicenseNumber())
                .user(user)
                .build();

        doctorRepo.save(doctor);
    }

    private void sendRegistrationEmail(RegistrationRequest request,UserEntity user)
    {

        NotificationDto welcomeEmail = NotificationDto.builder()
                .recipient(user.getEmail())
                .subject("Welcome to DAT Health!")
                .templateName("welcome")
                .message("Thank you for registering Your account is ready.")
                .templateVariables(Map.of(
                        "name", request.getName(),
                        "loginLink", loginLink
                ))
                .build();

        notificationService.sendMail(welcomeEmail, user);
    }

    private LocalDateTime calculateExpiryDate() {
        return LocalDateTime.now().plusHours(5);
    }

}
