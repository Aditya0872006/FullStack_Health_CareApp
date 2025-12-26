package com.example.HealthCareApp.doctor.repository;

import com.example.HealthCareApp.doctor.entity.Doctor;
import com.example.HealthCareApp.enums.Specilization;
import com.example.HealthCareApp.users.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DoctorRepo extends JpaRepository<Doctor, Long> {

    Optional<Doctor> findByUser(UserEntity user);

    List<Doctor> findBySpecilization(Specilization specilization);

}
