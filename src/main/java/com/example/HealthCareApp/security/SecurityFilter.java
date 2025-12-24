package com.example.HealthCareApp.security;


import com.example.HealthCareApp.exception.CustomAccessDeniedHandler;
import com.example.HealthCareApp.exception.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity   // Enables @PreAuthorize, @PostAuthorize
@RequiredArgsConstructor
public class SecurityFilter {

    // JWT filter – validates token and sets SecurityContext
    private final AuthFilter authFilter;

    // Handles 401 Unauthorized (no/invalid token)
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    // Handles 403 Forbidden (no permission)
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                // Disable CSRF because we are using JWT (stateless)
                .csrf(AbstractHttpConfigurer::disable)

                // Enable CORS (frontend access)
                .cors(Customizer.withDefaults())

                // Custom exception handling
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(customAuthenticationEntryPoint) // 401
                        .accessDeniedHandler(customAccessDeniedHandler))           // 403

                // Authorization rules
                .authorizeHttpRequests(req -> req
                        .requestMatchers("/api/auth/**", "/api/doctors/**","/api/roles/**").permitAll()
                        .anyRequest().authenticated()) // All other APIs need JWT

                // No session – every request must send JWT
                .sessionManagement(mag ->
                        mag.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // JWT filter runs before Spring’s default auth filter
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

    // Password encoder for hashing passwords
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AuthenticationManager used during login
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
