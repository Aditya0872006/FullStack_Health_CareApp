package com.example.HealthCareApp.consultation.service;

import com.example.HealthCareApp.appointment.entity.Appointment;
import com.example.HealthCareApp.appointment.repository.AppointmentRepo;
import com.example.HealthCareApp.consultation.dto.ConsultationDto;
import com.example.HealthCareApp.consultation.entity.ConsultationEntity;
import com.example.HealthCareApp.consultation.repository.ConsultationRepo;
import com.example.HealthCareApp.enums.AppintmentStatus;
import com.example.HealthCareApp.exception.BadRequestException;
import com.example.HealthCareApp.exception.NotFoundExecption;
import com.example.HealthCareApp.patient.entity.Patient;
import com.example.HealthCareApp.patient.repository.PatientRepo;
import com.example.HealthCareApp.res.Response;
import com.example.HealthCareApp.users.Entity.UserEntity;
import com.example.HealthCareApp.users.Service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsultationServiceImp implements ConsultationService{

    private final ConsultationRepo consultationRepo;
    private final AppointmentRepo appointmentRepo;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final PatientRepo patientRepo;

    @Override
    @Transactional
    public Response<ConsultationDto> createConsultation(ConsultationDto consultationDTO)
    {
        UserEntity user = userService.getCurrentUser();

        Long appointmentId = consultationDTO.getAppointmentId();

        Appointment appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new NotFoundExecption("Appointment not found."));

        // Security Check 1: Must be the doctor linked to the appointment
        if (!appointment.getDoctor().getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You are not authorized to create notes for this consultation.");
        }
        // Complete the appointment
        appointment.setStatus(AppintmentStatus.COMPLETED);
        appointmentRepo.save(appointment);

        // Check 3: Ensure a consultation doesn't already exist for this appointment
        if (consultationRepo.findByAppointmentId(appointmentId).isPresent()) {
            throw new BadRequestException("Consultation notes already exist for this appointment.");
        }

        ConsultationEntity consultation = ConsultationEntity.builder()
                .consultationDate(LocalDateTime.now())
                .subjectiveNotes(consultationDTO.getSubjectiveNotes())
                .objectiveFindings(consultationDTO.getObjectiveFindings())
                .assessment(consultationDTO.getAssessment())
                .plan(consultationDTO.getPlan())
                .appointment(appointment)
                .build();

        consultationRepo.save(consultation);

        return Response.<ConsultationDto>builder()
                .statusCode(200)
                .data(modelMapper.map(consultation, ConsultationDto.class))
                .message("Consultation notes saved successfully.")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Response<ConsultationDto> getConsultationByAppointmentId(Long appointmentId)
    {

        ConsultationEntity consultation = consultationRepo.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new NotFoundExecption("Consultation notes not found for appointment ID: " + appointmentId));


        return Response.<ConsultationDto>builder()
                .statusCode(200)
                .message("Consultation notes retrieved successfully.")
                .data(modelMapper.map(consultation, ConsultationDto.class))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Response<List<ConsultationDto>> getConsultationHistoryForPatient(Long patientId)
    {
        UserEntity user = userService.getCurrentUser();

        // 1. If patientId is null, retrieve the ID of the current authenticated patient.
        if (patientId == null) {
            Patient currentPatient = patientRepo.findByUser(user)
                    .orElseThrow(() -> new BadRequestException("Patient profile not found for the current user"));
            patientId = currentPatient.getId();
        }

        // Find the patient to ensure they exist (or to perform future security checks)
        patientRepo.findById(patientId)
                .orElseThrow(() -> new NotFoundExecption("Patient not found "));


        // Use the repository method to fetch all consultations linked via appointments
        List<ConsultationEntity> history = consultationRepo.findByAppointmentPatientIdOrderByConsultationDateDesc(patientId);

        if (history.isEmpty()) {
            return Response.<List<ConsultationDto>>builder()
                    .statusCode(200)
                    .message("No consultation history found for this patient.")
                    .data(List.of())
                    .build();
        }

        List<ConsultationDto> historyDTOs = history.stream()
                .map(consultation -> modelMapper.map(consultation, ConsultationDto.class))
                .toList();

        return Response.<List<ConsultationDto>>builder()
                .statusCode(200)
                .message("Consultation history retrieved successfully.")
                .data(historyDTOs)
                .build();


    }
}
