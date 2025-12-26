package com.example.HealthCareApp.patient.entity;





import com.example.HealthCareApp.appointment.entity.Appointment;
import com.example.HealthCareApp.enums.BloodGroup;
import com.example.HealthCareApp.enums.Genotype;
import com.example.HealthCareApp.users.Entity.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "patients")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String phone;

    // Essential Medical Fields (simplified for a remote system)

    @Lob // Stores allergies as a comma-separated string
    private String knownAllergies;

    @Enumerated(EnumType.STRING)
    private BloodGroup bloodGroup;

    @Enumerated(EnumType.STRING)
    private Genotype genotype;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private UserEntity user;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Appointment> appointments;
}