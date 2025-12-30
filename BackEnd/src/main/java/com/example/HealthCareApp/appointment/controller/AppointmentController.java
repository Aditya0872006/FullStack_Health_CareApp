package com.example.HealthCareApp.appointment.controller;

import com.example.HealthCareApp.appointment.dto.AppointmentDto;
import com.example.HealthCareApp.appointment.dto.RescheduleAppointmentDto;
import com.example.HealthCareApp.appointment.service.AppointmentService;
import com.example.HealthCareApp.res.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<Response<AppointmentDto>> bookAppointment(@RequestBody @Valid AppointmentDto appointmentDTO){
        return ResponseEntity.ok(appointmentService.bookAppointment(appointmentDTO));
    }

    @GetMapping
    public  ResponseEntity<Response<List<AppointmentDto>>> getMyAppointments(){
        return ResponseEntity.ok(appointmentService.getMyAppointments());
    }

    @PutMapping("/cancel/{appointmentId}")
    public  ResponseEntity<Response<AppointmentDto>> cancelAppointment(@PathVariable Long appointmentId){
        return ResponseEntity.ok(appointmentService.cancelAppointment(appointmentId));
    }

    @PutMapping("/complete/{appointmentId}")
    @PreAuthorize(("hasAuthority('DOCTOR')"))
    public  ResponseEntity<Response<?>> completeAppointment(@PathVariable Long appointmentId){
        return ResponseEntity.ok(appointmentService.completeAppointment(appointmentId));
    }

    @PutMapping("/{id}/reschedule")
    public ResponseEntity<Response<AppointmentDto>>
    rescheduleAppointment(@PathVariable Long appointmentId,
                            @RequestBody RescheduleAppointmentDto dto)
    {
        return ResponseEntity.ok(appointmentService.rescheduleAppointment(appointmentId,dto));
    }

}