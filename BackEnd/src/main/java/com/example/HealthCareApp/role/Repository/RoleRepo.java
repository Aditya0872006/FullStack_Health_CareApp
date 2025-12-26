package com.example.HealthCareApp.role.Repository;

import com.example.HealthCareApp.role.Entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepo extends JpaRepository<RoleEntity, Long> {

    Optional<RoleEntity> findByName(String name);
}