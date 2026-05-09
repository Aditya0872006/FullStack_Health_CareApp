package com.example.HealthCareApp.appointment.listener;

import com.example.HealthCareApp.appointment.event.*;
import com.example.HealthCareApp.notification.dto.NotificationDto;
import com.example.HealthCareApp.notification.service.NotificationService;
import com.example.HealthCareApp.users.Entity.UserEntity;
import com.example.HealthCareApp.users.Repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Subscriber: handles ALL email notification side-effects for appointment events.
 * The service layer has zero knowledge this class exists.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AppointmentNotificationListener {

    private final NotificationService notificationService;
    private final UserRepo userRepository;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("EEEE, MMM dd, yyyy 'at' hh:mm a");

    // ------------------------------------------------------------------ BOOKED

    @EventListener
    public void handleAppointmentBooked(AppointmentBookedEvent event) {
        log.info("[Notification] Handling AppointmentBookedEvent for appointmentId={}", event.getAppointmentId());

        sendPatientBookingConfirmation(event);
        sendDoctorBookingNotification(event);
    }

    private void sendPatientBookingConfirmation(AppointmentBookedEvent event) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("patientName",            event.getPatientName());
        vars.put("doctorName",             event.getDoctorName());
        vars.put("appointmentTime",        event.getAppointmentTime().format(FORMATTER));
        vars.put("isVirtual",              true);
        vars.put("meetingLink",            event.getMeetingLink());
        vars.put("purposeOfConsultation",  event.getPurposeOfConsultation());

        UserEntity patientUser = userRepository.findByEmail(event.getPatientEmail())
                .orElseThrow();

        notificationService.sendMail(
                NotificationDto.builder()
                        .recipient(event.getPatientEmail())
                        .subject("HealthCare_App: Your Appointment is Confirmed")
                        .templateName("patient-appointment")
                        .templateVariables(vars)
                        .build(),
                patientUser
        );
    }

    private void sendDoctorBookingNotification(AppointmentBookedEvent event) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("doctorName",             event.getDoctorName());
        vars.put("patientFullName",        event.getPatientName());
        vars.put("appointmentTime",        event.getAppointmentTime().format(FORMATTER));
        vars.put("isVirtual",              true);
        vars.put("meetingLink",            event.getMeetingLink());
        vars.put("initialSymptoms",        event.getInitialSymptoms());
        vars.put("purposeOfConsultation",  event.getPurposeOfConsultation());

        UserEntity doctorUser = userRepository.findByEmail(event.getDoctorEmail())
                .orElseThrow();

        notificationService.sendMail(
                NotificationDto.builder()
                        .recipient(event.getDoctorEmail())
                        .subject("HealthCare_App: New Appointment Booked")
                        .templateName("doctor-appointment")
                        .templateVariables(vars)
                        .build(),
                doctorUser
        );
    }

    // --------------------------------------------------------------- CANCELLED

    @EventListener
    public void handleAppointmentCancelled(AppointmentCancelledEvent event) {
        log.info("[Notification] Handling AppointmentCancelledEvent for appointmentId={}", event.getAppointmentId());

        Map<String, Object> baseVars = new HashMap<>();
        baseVars.put("cancellingPartyName", event.getCancelledByName());
        baseVars.put("appointmentTime",     event.getAppointmentTime().format(FORMATTER));
        baseVars.put("doctorName",          event.getDoctorName());
        baseVars.put("patientFullName",     event.getPatientName());
        baseVars.put("isVirtual",           true);

        // Notify doctor
        Map<String, Object> doctorVars = new HashMap<>(baseVars);
        doctorVars.put("recipientName", event.getDoctorName());

        UserEntity doctorUser = userRepository.findByEmail(event.getDoctorEmail()).orElseThrow();
        notificationService.sendMail(
                NotificationDto.builder()
                        .recipient(event.getDoctorEmail())
                        .subject("DAT Health: Appointment Cancellation")
                        .templateName("appointment-cancellation")
                        .templateVariables(doctorVars)
                        .build(),
                doctorUser
        );

        // Notify patient
        Map<String, Object> patientVars = new HashMap<>(baseVars);
        patientVars.put("recipientName", event.getPatientName());

        UserEntity patientUser = userRepository.findByEmail(event.getPatientEmail()).orElseThrow();
        notificationService.sendMail(
                NotificationDto.builder()
                        .recipient(event.getPatientEmail())
                        .subject("DAT Health: Appointment CANCELED (ID: " + event.getAppointmentId() + ")")
                        .templateName("appointment-cancellation")
                        .templateVariables(patientVars)
                        .build(),
                patientUser
        );
    }

    // ------------------------------------------------------------- RESCHEDULED

    @EventListener
    public void handleAppointmentRescheduled(AppointmentRescheduledEvent event) {
        log.info("[Notification] Handling AppointmentRescheduledEvent for appointmentId={}", event.getAppointmentId());

        Map<String, Object> vars = new HashMap<>();
        vars.put("patientName",           event.getPatientName());
        vars.put("doctorName",            event.getDoctorName());
        vars.put("appointmentTime",       event.getNewAppointmentTime().format(FORMATTER));
        vars.put("meetingLink",           event.getMeetingLink());
        vars.put("purposeOfConsultation", event.getPurposeOfConsultation());
        vars.put("isVirtual",             true);

        UserEntity patientUser = userRepository.findByEmail(event.getPatientEmail()).orElseThrow();
        notificationService.sendMail(
                NotificationDto.builder()
                        .recipient(event.getPatientEmail())
                        .subject("HealthCare_App: Your Appointment Has Been Rescheduled")
                        .templateName("patient-appointment")
                        .templateVariables(vars)
                        .build(),
                patientUser
        );

        UserEntity doctorUser = userRepository.findByEmail(event.getDoctorEmail()).orElseThrow();
        notificationService.sendMail(
                NotificationDto.builder()
                        .recipient(event.getDoctorEmail())
                        .subject("HealthCare_App: Appointment Rescheduled")
                        .templateName("doctor-appointment")
                        .templateVariables(vars)
                        .build(),
                doctorUser
        );
    }

    // --------------------------------------------------------------- COMPLETED

    @EventListener
    public void handleAppointmentCompleted(AppointmentCompletedEvent event) {
        log.info("[Notification] AppointmentCompletedEvent — no email required for appointmentId={}. " +
                "Add template call here if needed.", event.getAppointmentId());
        // Wire in a completion summary email here when the template is ready.
    }
}