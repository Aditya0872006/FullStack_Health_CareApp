package com.example.HealthCareApp.users.Repository;

import com.example.HealthCareApp.users.Entity.PasswordResetCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetRepo extends JpaRepository<PasswordResetCode, Long> {

    Optional<PasswordResetCode> findByCode(String code);
    void deleteByUserId(Long userId);

}
