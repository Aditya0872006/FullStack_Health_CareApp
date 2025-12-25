package com.example.HealthCareApp.users.Controller;

import com.example.HealthCareApp.res.Response;
import com.example.HealthCareApp.users.Dto.RegistrationRequest;
import com.example.HealthCareApp.users.Service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController
{
    private final AuthService authService;
    @PostMapping("/register")
    ResponseEntity<Response<String>> register(@RequestBody @Valid RegistrationRequest registrationRequest)
    {
        return ResponseEntity.ok(authService.registerUser(registrationRequest));
    }
}
