package com.example.HealthCareApp.consultation.service;

import com.example.HealthCareApp.consultation.dto.ConsultationDto;
import com.example.HealthCareApp.res.Response;

import java.util.List;

public interface ConsultationService {

    Response<ConsultationDto> createConsultation(ConsultationDto consultationDTO);

    Response<ConsultationDto> getConsultationByAppointmentId(Long appointmentId);

    Response<List<ConsultationDto>> getConsultationHistoryForPatient(Long patientId);

}