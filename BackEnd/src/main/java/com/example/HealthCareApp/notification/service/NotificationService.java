package com.example.HealthCareApp.notification.service;

import com.example.HealthCareApp.notification.dto.NotificationDto;
import com.example.HealthCareApp.users.Entity.UserEntity;

public interface NotificationService
{
    public void sendMail(NotificationDto notificationDto, UserEntity user);
}
