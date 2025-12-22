package com.example.HealthCareApp.patient.repository;

import com.example.HealthCareApp.patient.entity.Patient;
import com.example.HealthCareApp.users.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRepo extends JpaRepository<Patient,Long>
{
    Optional<Patient> findByUser(UserEntity user);
}
