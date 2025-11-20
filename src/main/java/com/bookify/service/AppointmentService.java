package com.bookify.service;

import com.bookify.dto.AppointmentDTO;
import com.bookify.entity.Appointment;
import com.bookify.entity.User;
import com.bookify.enums.AppointmentStatus;
import com.bookify.exception.AppointmentConflictException;
import com.bookify.exception.ResourceNotFoundException;
import com.bookify.repository.AppointmentRepository;
import com.bookify.repository.ServiceRepository;
import com.bookify.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Business logic for Appointment management
 */
@Service
@Transactional
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    public List<AppointmentDTO> getAllAppointments() {
        return appointmentRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public AppointmentDTO getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", id));
        return convertToDTO(appointment);
    }

    public List<AppointmentDTO> getAppointmentsByClientId(Long clientId) {
        return appointmentRepository.findByClientId(clientId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AppointmentDTO> getAppointmentsByStatus(AppointmentStatus status) {
        return appointmentRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public AppointmentDTO createAppointment(AppointmentDTO appointmentDTO) {
        // Validate client exists
        User client = userRepository.findById(appointmentDTO.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("User", appointmentDTO.getClientId()));

        // Validate service exists
        com.bookify.entity.Service service = serviceRepository.findById(appointmentDTO.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service", appointmentDTO.getServiceId()));

        // Check for conflicts
        if (appointmentRepository.existsConflictingAppointment(
                service.getId(), appointmentDTO.getAppointmentDateTime())) {
            throw new AppointmentConflictException();
        }

        Appointment appointment = Appointment.builder()
                .client(client)
                .service(service)
                .appointmentDateTime(appointmentDTO.getAppointmentDateTime())
                .status(AppointmentStatus.PENDING)
                .notes(appointmentDTO.getNotes())
                .build();

        Appointment saved = appointmentRepository.save(appointment);
        return convertToDTO(saved);
    }

    public AppointmentDTO updateAppointment(Long id, AppointmentDTO appointmentDTO) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", id));

        appointment.setAppointmentDateTime(appointmentDTO.getAppointmentDateTime());
        appointment.setStatus(appointmentDTO.getStatus());
        appointment.setNotes(appointmentDTO.getNotes());

        Appointment updated = appointmentRepository.save(appointment);
        return convertToDTO(updated);
    }

    public void deleteAppointment(Long id) {
        if (!appointmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Appointment", id);
        }
        appointmentRepository.deleteById(id);
    }

    private AppointmentDTO convertToDTO(Appointment appointment) {
        return AppointmentDTO.builder()
                .id(appointment.getId())
                .clientId(appointment.getClient().getId())
                .clientName(appointment.getClient().getFullName())
                .clientEmail(appointment.getClient().getEmail())
                .serviceId(appointment.getService().getId())
                .serviceName(appointment.getService().getName())
                .serviceDuration(appointment.getService().getDurationMinutes())
                .appointmentDateTime(appointment.getAppointmentDateTime())
                .status(appointment.getStatus())
                .notes(appointment.getNotes())
                .createdAt(appointment.getCreatedAt())
                .updatedAt(appointment.getUpdatedAt())
                .build();
    }
}