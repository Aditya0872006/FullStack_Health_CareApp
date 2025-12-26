package com.example.HealthCareApp.appointment.service;

import com.example.HealthCareApp.appointment.dto.AppointmentDto;
import com.example.HealthCareApp.appointment.entity.Appointment;
import com.example.HealthCareApp.appointment.repository.AppointmentRepo;
import com.example.HealthCareApp.doctor.entity.Doctor;
import com.example.HealthCareApp.doctor.repository.DoctorRepo;
import com.example.HealthCareApp.enums.AppintmentStatus;
import com.example.HealthCareApp.exception.BadRequestException;
import com.example.HealthCareApp.exception.NotFoundExecption;
import com.example.HealthCareApp.notification.dto.NotificationDto;
import com.example.HealthCareApp.notification.service.NotificationService;
import com.example.HealthCareApp.patient.entity.Patient;
import com.example.HealthCareApp.patient.repository.PatientRepo;
import com.example.HealthCareApp.res.Response;
import com.example.HealthCareApp.users.Entity.UserEntity;
import com.example.HealthCareApp.users.Service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentServiceImp implements AppointmentService
{
    private final AppointmentRepo appointmentRepo;
    private final PatientRepo patientRepo;
    private final DoctorRepo doctorRepo;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final NotificationService notificationService;


    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("EEEE, MMM dd, yyyy 'at' hh:mm a");
    @Override
    public Response<AppointmentDto> bookAppointment(AppointmentDto appointmentDTO)
    {
        UserEntity currentuser=userService.getCurrentUser();

        // 1. Get the patient initiating the booking
        Patient patient = patientRepo.findByUser(currentuser)
                .orElseThrow(() -> new NotFoundExecption("Patient profile required for booking."));

        // 2. Get the target doctor
        Doctor doctor = doctorRepo.findById(appointmentDTO.getDoctorId())
                .orElseThrow(() -> new NotFoundExecption("Doctor not found."));


        // --- START: VALIDATION LOGIC ---
        // Define the proposed time slot and the end time
        LocalDateTime startTime = appointmentDTO.getStartTime();
        LocalDateTime endTime = startTime.plusMinutes(60); // Assuming 60-min slot

        // 3. Basic validation: booking must be at least 1 hour in advance
        if (startTime.isBefore(LocalDateTime.now().plusHours(1))) {
            throw new BadRequestException("Appointments must be booked at least 1 hour in advance.");
        }

        //This code snippet logic used to enforce a mandatory one-hour break (or buffer) for the doctor before a new appointment.
        LocalDateTime checkStart = startTime.minusMinutes(60);


        // We only need to check for existing appointments whose END TIME overlaps with
        // the proposed start time, OR whose START TIME overlaps with the proposed end time.

        List<Appointment> conflicts = appointmentRepo.findConflictingAppointments(
                doctor.getId(),
                checkStart, // Check for conflicts from 1 hour before the proposed start
                endTime
        );

        if (!conflicts.isEmpty()) {
            throw new BadRequestException("Doctor is not available at the requested time. Please check their schedule.");
        }

        // 4a. Generate a unique, random string for the room name.
        //    (Your existing code is good for this)
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String uniqueRoomName = "HealthCare-" + uuid.substring(0, 10);


        // 4b. Use the public Jitsi Meet domain with your unique room name
        String meetingLink = "https://meet.jit.si/" + uniqueRoomName;



        // 5. Build and Save Appointment
        Appointment appointment = Appointment.builder()
                .startTime(appointmentDTO.getStartTime())
                .endTime(appointmentDTO.getStartTime().plusMinutes(60)) // Assuming 60-min slot
                .meetingLink(meetingLink)
                .initialSymptoms(appointmentDTO.getInitialSymptoms())
                .purposeOfConsultation(appointmentDTO.getPurposeOfConsultation())
                .status(AppintmentStatus.SCHEDULED)
                .doctor(doctor)
                .patient(patient)
                .build();

        Appointment savedAppointment = appointmentRepo.save(appointment);

        sendAppointmentConfirmation(savedAppointment);

        return Response.<AppointmentDto>builder()
                .statusCode(200)
                .message("Appointment booked successfully.")
                .build();

    }

    @Override
    public Response<List<AppointmentDto>> getMyAppointments() {
        return null;
    }

    @Override
    public Response<AppointmentDto> cancelAppointment(Long appointmentId) {
        return null;
    }

    @Override
    public Response<?> completeAppointment(Long appointmentId) {
        return null;
    }

    private void sendAppointmentConfirmation(Appointment appointment) {

        // --- 1. Prepare Patient Notification ---
        UserEntity patientUser = appointment.getPatient().getUser();
        String formattedTime = appointment.getStartTime().format(FORMATTER);


        Map<String, Object> patientVars = new HashMap<>();
        patientVars.put("patientName", patientUser.getName());
        patientVars.put("doctorName", appointment.getDoctor().getUser().getName());
        patientVars.put("appointmentTime", formattedTime);
        patientVars.put("isVirtual", true);
        patientVars.put("meetingLink", appointment.getMeetingLink());
        patientVars.put("purposeOfConsultation", appointment.getPurposeOfConsultation());

        NotificationDto patientNotification = NotificationDto.builder()
                .recipient(patientUser.getEmail())
                .subject("DAT Health: Your Appointment is Confirmed")
                .templateName("patient-appointment")
                .templateVariables(patientVars)
                .build();


        // Dispatch patient email using the low-level service
        notificationService.sendMail(patientNotification, patientUser);


        // --- 2. Prepare Doctor Notification ---
        UserEntity doctorUser = appointment.getDoctor().getUser();

        Map<String, Object> doctorVars = new HashMap<>();
        doctorVars.put("doctorName", doctorUser.getName());
        doctorVars.put("patientFullName", patientUser.getName());
        doctorVars.put("appointmentTime", formattedTime);
        doctorVars.put("isVirtual", true);
        doctorVars.put("meetingLink", appointment.getMeetingLink());
        doctorVars.put("initialSymptoms", appointment.getInitialSymptoms());
        doctorVars.put("purposeOfConsultation", appointment.getPurposeOfConsultation());

        NotificationDto doctorNotification = NotificationDto.builder()
                .recipient(doctorUser.getEmail())
                .subject("DAT Health: New Appointment Booked")
                .templateName("doctor-appointment")
                .templateVariables(doctorVars)
                .build();


        // Dispatch doctor email using the low-level service
        notificationService.sendMail(doctorNotification, doctorUser);
    }


}

