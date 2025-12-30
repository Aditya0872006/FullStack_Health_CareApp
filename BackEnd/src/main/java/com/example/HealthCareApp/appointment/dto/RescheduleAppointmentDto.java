package com.example.HealthCareApp.appointment.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RescheduleAppointmentDto {
    private LocalDateTime newStartTime;
}

