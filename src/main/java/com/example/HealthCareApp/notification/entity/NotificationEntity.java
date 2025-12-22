package com.example.HealthCareApp.notification.entity;

import com.example.HealthCareApp.enums.NotificationType;
import com.example.HealthCareApp.users.Entity.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subject;
    private String recipient;

    private String message;

    @Enumerated(EnumType.STRING)
    private NotificationType type; // EMAIL, SMS, PUSH

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    private final LocalDateTime createdAt = LocalDateTime.now();
}
