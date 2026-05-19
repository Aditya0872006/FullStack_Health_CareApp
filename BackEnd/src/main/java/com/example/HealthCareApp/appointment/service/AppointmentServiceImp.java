package com.example.HealthCareApp.appointment.service;

import com.example.HealthCareApp.appointment.dto.AppointmentDto;
import com.example.HealthCareApp.appointment.dto.RescheduleAppointmentDto;
import com.example.HealthCareApp.appointment.entity.Appointment;
import com.example.HealthCareApp.appointment.event.*;
import com.example.HealthCareApp.appointment.repository.AppointmentRepo;
import com.example.HealthCareApp.doctor.entity.Doctor;
import com.example.HealthCareApp.doctor.repository.DoctorRepo;
import com.example.HealthCareApp.enums.AppintmentStatus;
import com.example.HealthCareApp.exception.BadRequestException;
import com.example.HealthCareApp.exception.NotFoundExecption;
import com.example.HealthCareApp.patient.entity.Patient;
import com.example.HealthCareApp.patient.repository.PatientRepo;
import com.example.HealthCareApp.res.Response;
import com.example.HealthCareApp.users.Entity.UserEntity;
import com.example.HealthCareApp.users.Service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentServiceImp implements AppointmentService {

    private final AppointmentRepo        appointmentRepo;
    private final PatientRepo            patientRepo;
    private final DoctorRepo             doctorRepo;
    private final UserService            userService;
    private final ModelMapper            modelMapper;
    private final ApplicationEventPublisher eventPublisher; // ← replaces NotificationService

    // ----------------------------------------------------------------- BOOK

    @Override
    @Transactional
    public Response<AppointmentDto> bookAppointment(AppointmentDto appointmentDTO) {

        UserEntity currentUser = userService.getCurrentUser();

        Patient patient = patientRepo.findByUser(currentUser)
                .orElseThrow(() -> new NotFoundExecption("Patient profile required for booking."));

        Doctor doctor = doctorRepo.findById(appointmentDTO.getDoctorId())
                .orElseThrow(() -> new NotFoundExecption("Doctor not found."));

        LocalDateTime startTime = appointmentDTO.getStartTime();
        LocalDateTime endTime   = startTime.plusMinutes(60);

        int hour = startTime.getHour();

        if (hour < 9 || hour >= 18) {
            throw new BadRequestException("Appointments can only be booked between 9 AM to 6 PM.");
        }

        if (startTime.isBefore(LocalDateTime.now().plusHours(1))) {
            throw new BadRequestException("Appointments must be booked at least 1 hour in advance.");
        }

        List<Appointment> conflicts = appointmentRepo.findConflictingAppointments(
                doctor.getId(), startTime.minusMinutes(60), endTime);

        if (!conflicts.isEmpty()) {
            throw new BadRequestException("Doctor is not available at the requested time.");
        }

        String meetingLink = "https://meet.jit.si/HealthCare-" +
                UUID.randomUUID().toString().replace("-", "").substring(0, 10);

        Appointment appointment = Appointment.builder()
                .startTime(startTime)
                .endTime(endTime)
                .meetingLink(meetingLink)
                .initialSymptoms(appointmentDTO.getInitialSymptoms())
                .purposeOfConsultation(appointmentDTO.getPurposeOfConsultation())
                .status(AppintmentStatus.SCHEDULED)
                .doctor(doctor)
                .patient(patient)
                .build();

        Appointment saved = appointmentRepo.save(appointment);

        // ✅ Publish — service is done here; listeners handle the rest
        eventPublisher.publishEvent(
                AppointmentBookedEvent.builder()
                        .appointmentId(saved.getId())
                        .patientName(patient.getUser().getName())
                        .patientEmail(patient.getUser().getEmail())
                        .doctorName(doctor.getUser().getName())
                        .doctorEmail(doctor.getUser().getEmail())
                        .appointmentTime(saved.getStartTime())
                        .meetingLink(saved.getMeetingLink())
                        .initialSymptoms(saved.getInitialSymptoms())
                        .purposeOfConsultation(saved.getPurposeOfConsultation())
                        .build()
        );

        return Response.<AppointmentDto>builder()
                .statusCode(200)
                .message("Appointment booked successfully.")
                .build();
    }

    // ------------------------------------------------------------- GET ALL

    @Override
    @Transactional
    public Response<List<AppointmentDto>> getMyAppointments() {

        UserEntity user   = userService.getCurrentUser();
        Long       userId = user.getId();
        List<Appointment> appointments;

        boolean isDoctor = user.getRoles().stream()
                .anyMatch(r -> r.getName().equals("DOCTOR"));

        if (isDoctor) {
            doctorRepo.findByUser(user)
                    .orElseThrow(() -> new NotFoundExecption("Doctor profile not found."));
            appointments = appointmentRepo.findByDoctor_User_IdOrderByIdDesc(userId);
        } else {
            patientRepo.findByUser(user)
                    .orElseThrow(() -> new NotFoundExecption("Patient profile not found."));
            appointments = appointmentRepo.findByPatient_User_IdOrderByIdDesc(userId);
        }

        List<AppointmentDto> dtos = appointments.stream()
                .map(a -> modelMapper.map(a, AppointmentDto.class))
                .toList();

        return Response.<List<AppointmentDto>>builder()
                .statusCode(200)
                .message("Appointments retrieved successfully.")
                .data(dtos)
                .build();
    }

    // -------------------------------------------------------------- CANCEL

    @Override
    @Transactional
    public Response<AppointmentDto> cancelAppointment(Long appointmentId) {

        UserEntity  user        = userService.getCurrentUser();
        Appointment appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new NotFoundExecption("Appointment not found."));

        boolean isOwner =
                appointment.getPatient().getUser().getId().equals(user.getId()) ||
                        appointment.getDoctor().getUser().getId().equals(user.getId());

        if (!isOwner) {
            throw new BadRequestException("You do not have permission to cancel this appointment.");
        }

        appointment.setStatus(AppintmentStatus.CANCELLED);
        Appointment saved = appointmentRepo.save(appointment);

        // ✅ Publish
        eventPublisher.publishEvent(
                AppointmentCancelledEvent.builder()
                        .appointmentId(saved.getId())
                        .patientName(saved.getPatient().getUser().getName())
                        .patientEmail(saved.getPatient().getUser().getEmail())
                        .doctorName(saved.getDoctor().getUser().getName())
                        .doctorEmail(saved.getDoctor().getUser().getEmail())
                        .appointmentTime(saved.getStartTime())
                        .cancelledByName(user.getName())
                        .build()
        );

        return Response.<AppointmentDto>builder()
                .statusCode(200)
                .message("Appointment cancelled successfully.")
                .build();
    }

    // ------------------------------------------------------------ COMPLETE

    @Override
    @Transactional
    public Response<?> completeAppointment(Long appointmentId) {

        UserEntity  currentUser = userService.getCurrentUser();
        Appointment appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new NotFoundExecption("Appointment not found: " + appointmentId));

        if (!appointment.getDoctor().getUser().getId().equals(currentUser.getId())) {
            throw new BadRequestException("Only the assigned doctor can mark this appointment as complete.");
        }

        appointment.setStatus(AppintmentStatus.COMPLETED);
        appointment.setEndTime(LocalDateTime.now());
        Appointment saved = appointmentRepo.save(appointment);

        // ✅ Publish
        eventPublisher.publishEvent(
                AppointmentCompletedEvent.builder()
                        .appointmentId(saved.getId())
                        .patientName(saved.getPatient().getUser().getName())
                        .doctorName(saved.getDoctor().getUser().getName())
                        .completedAt(saved.getEndTime())
                        .build()
        );

        return Response.builder()
                .statusCode(200)
                .message("Appointment successfully marked as completed.")
                .build();
    }

    // --------------------------------------------------------- RESCHEDULE

    @Override
    @Transactional
    public Response<AppointmentDto> rescheduleAppointment(
            Long appointmentId, RescheduleAppointmentDto dto) {

        UserEntity  currentUser = userService.getCurrentUser();
        Appointment appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new NotFoundExecption("Appointment not found."));

        if (appointment.getStatus() != AppintmentStatus.SCHEDULED) {
            throw new BadRequestException("Only scheduled appointments can be rescheduled.");
        }

        if (!appointment.getPatient().getUser().getId().equals(currentUser.getId())) {
            throw new BadRequestException("Unauthorized action.");
        }

        LocalDateTime newStart = dto.getNewStartTime();
        LocalDateTime newEnd   = newStart.plusMinutes(60);

        if (newStart.isBefore(LocalDateTime.now().plusHours(1))) {
            throw new BadRequestException("Rescheduling must be done at least 1 hour in advance.");
        }

        List<Appointment> conflicts = appointmentRepo.findConflictingAppointments(
                appointment.getDoctor().getId(), newStart.minusMinutes(60), newEnd);

        conflicts.removeIf(a -> a.getId().equals(appointmentId));

        if (!conflicts.isEmpty()) {
            throw new BadRequestException("Doctor is not available at this time.");
        }

        appointment.setStartTime(newStart);
        appointment.setEndTime(newEnd);
        Appointment saved = appointmentRepo.save(appointment);

        // ✅ Publish
        eventPublisher.publishEvent(
                AppointmentRescheduledEvent.builder()
                        .appointmentId(saved.getId())
                        .patientName(saved.getPatient().getUser().getName())
                        .patientEmail(saved.getPatient().getUser().getEmail())
                        .doctorName(saved.getDoctor().getUser().getName())
                        .doctorEmail(saved.getDoctor().getUser().getEmail())
                        .newAppointmentTime(saved.getStartTime())
                        .meetingLink(saved.getMeetingLink())
                        .purposeOfConsultation(saved.getPurposeOfConsultation())
                        .build()
        );

        return Response.<AppointmentDto>builder()
                .statusCode(200)
                .message("Appointment rescheduled successfully.")
                .build();
    }
}