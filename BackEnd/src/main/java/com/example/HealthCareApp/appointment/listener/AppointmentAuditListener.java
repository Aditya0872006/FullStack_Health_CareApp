package com.example.HealthCareApp.appointment.listener;

import com.example.HealthCareApp.appointment.event.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Subscriber: pure audit / structured logging for every appointment lifecycle event.
 * Zero coupling to any other bean — this class has no constructor dependencies.
 */
@Component
@Slf4j
public class AppointmentAuditListener {

    @EventListener
    public void onAppointmentBooked(AppointmentBookedEvent event) {
        log.info("[AUDIT] BOOKED  | appointmentId={} | patient='{}' | doctor='{}' | time='{}'",
                event.getAppointmentId(),
                event.getPatientName(),
                event.getDoctorName(),
                event.getAppointmentTime());
    }

    @EventListener
    public void onAppointmentCancelled(AppointmentCancelledEvent event) {
        log.warn("[AUDIT] CANCELLED | appointmentId={} | cancelledBy='{}' | patient='{}' | doctor='{}'",
                event.getAppointmentId(),
                event.getCancelledByName(),
                event.getPatientName(),
                event.getDoctorName());
    }

    @EventListener
    public void onAppointmentRescheduled(AppointmentRescheduledEvent event) {
        log.info("[AUDIT] RESCHEDULED | appointmentId={} | patient='{}' | doctor='{}' | newTime='{}'",
                event.getAppointmentId(),
                event.getPatientName(),
                event.getDoctorName(),
                event.getNewAppointmentTime());
    }

    @EventListener
    public void onAppointmentCompleted(AppointmentCompletedEvent event) {
        log.info("[AUDIT] COMPLETED | appointmentId={} | patient='{}' | doctor='{}' | completedAt='{}'",
                event.getAppointmentId(),
                event.getPatientName(),
                event.getDoctorName(),
                event.getCompletedAt());
    }
}