package com.example.HealthCareApp.users.Repository;

import com.example.HealthCareApp.users.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<UserEntity,Long>
{
    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);

}
