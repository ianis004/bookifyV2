package com.bookify.dto;

import com.bookify.enums.AppointmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 *Data Transfer Object for Appointment
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentDTO {

    private Long id;

    @NotNull(message = "Client ID is required")
    private Long clientId;

    private String clientName;

    private String clientEmail;

    @NotNull(message = "Service ID is required")
    private Long serviceId;

    private String serviceName;

    private Integer serviceDuration;

    @NotNull(message = "Appointment date and time is required")
    private LocalDateTime appointmentDateTime;

    private AppointmentStatus status;

    private String notes;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}