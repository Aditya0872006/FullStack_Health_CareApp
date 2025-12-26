package com.example.HealthCareApp.appointment.service;

import com.example.HealthCareApp.appointment.dto.AppointmentDto;
import com.example.HealthCareApp.res.Response;

import java.util.List;

public interface AppointmentService {

    Response<AppointmentDto> bookAppointment(AppointmentDto appointmentDTO);

    Response<List<AppointmentDto>> getMyAppointments();

    Response<AppointmentDto> cancelAppointment(Long appointmentId);

    Response<?> completeAppointment(Long appointmentId);

}
