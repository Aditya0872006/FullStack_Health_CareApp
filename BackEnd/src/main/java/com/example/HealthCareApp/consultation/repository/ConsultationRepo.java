package com.example.HealthCareApp.consultation.repository;

import com.example.HealthCareApp.consultation.entity.ConsultationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface ConsultationRepo extends JpaRepository<ConsultationEntity,Long>
{
    Optional<ConsultationEntity> findByAppointmentId(Long appointmentId);
    List<ConsultationEntity> findByAppointmentPatientIdOrderByConsultationDateDesc(Long patientId);
}
