package com.example.HealthCareApp.notification.repository;

import com.example.HealthCareApp.notification.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepo extends JpaRepository<NotificationEntity,Long>
{
}
