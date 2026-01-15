package com.bookify.service;

import com.bookify.dto.AppointmentDTO;
import com.bookify.dto.BusinessSettingsDTO;
import com.bookify.entity.Appointment;
import com.bookify.entity.User;
import com.bookify.enums.AppointmentStatus;
import com.bookify.exception.AppointmentConflictException;
import com.bookify.exception.ResourceNotFoundException;
import com.bookify.repository.AppointmentRepository;
import com.bookify.repository.ServiceRepository;
import com.bookify.repository.UserRepository;
import com.bookify.socket.AppointmentNotificationServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Business logic for Appointment management
 * Now includes socket-based notifications for real-time updates
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

    @Autowired
    private BusinessSettingsService businessSettingsService;

    @Autowired
    private AppointmentNotificationServer notificationServer;

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

    public List<AppointmentDTO> getAppointmentsByUsername(String username) {
        return appointmentRepository.findByClientUsername(username).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AppointmentDTO> getAppointmentsByStatus(AppointmentStatus status) {
        return appointmentRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public AppointmentDTO createAppointment(AppointmentDTO appointmentDTO) {

        User client = userRepository.findById(appointmentDTO.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("User", appointmentDTO.getClientId()));

        com.bookify.entity.Service service = serviceRepository.findById(appointmentDTO.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service", appointmentDTO.getServiceId()));

        BusinessSettingsDTO settings = businessSettingsService.getSettings();

        if (!settings.getAcceptOnlineBookings()) {
            throw new IllegalArgumentException("Online bookings are currently disabled. Please contact us directly.");
        }

        LocalTime appointmentTime = appointmentDTO.getAppointmentDateTime().toLocalTime();
        if (appointmentTime.isBefore(settings.getOpeningTime()) ||
                appointmentTime.isAfter(settings.getClosingTime())) {
            throw new IllegalArgumentException(
                    String.format("Appointments must be between %s and %s",
                            settings.getOpeningTime(),
                            settings.getClosingTime())
            );
        }

        LocalTime appointmentEndTime = appointmentTime.plusMinutes(service.getDurationMinutes());
        if (appointmentEndTime.isAfter(settings.getClosingTime())) {
            throw new IllegalArgumentException(
                    String.format("This appointment would end at %s, which is after closing time (%s)",
                            appointmentEndTime,
                            settings.getClosingTime())
            );
        }

        // Conflict check
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

        // NEW: Send notification via socket server
        try {
            String notification = String.format(
                    "NEW_APPOINTMENT|ID:%d|CLIENT:%s|SERVICE:%s|TIME:%s",
                    saved.getId(),
                    saved.getClient().getFullName(),
                    saved.getService().getName(),
                    saved.getAppointmentDateTime().toString()
            );
            notificationServer.broadcastNotification(notification);
            System.out.println("📢 Socket notification sent: New appointment created");
        } catch (Exception e) {
            System.err.println("⚠️ Failed to send socket notification: " + e.getMessage());
// Don't fail the appointment creation if notification fails
        }
        return convertToDTO(saved);
    }

    public AppointmentDTO updateAppointment(Long id, AppointmentDTO appointmentDTO) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", id));

        AppointmentStatus oldStatus = appointment.getStatus();

        appointment.setAppointmentDateTime(appointmentDTO.getAppointmentDateTime());
        appointment.setStatus(appointmentDTO.getStatus());
        appointment.setNotes(appointmentDTO.getNotes());

        Appointment updated = appointmentRepository.save(appointment);

        // NEW: Send notification via socket server when status changes
        try {
            if (oldStatus != appointmentDTO.getStatus()) {
                String notification = String.format(
                        "APPOINTMENT_UPDATED|ID:%d|CLIENT:%s|OLD_STATUS:%s|NEW_STATUS:%s|SERVICE:%s",
                        updated.getId(),
                        updated.getClient().getFullName(),
                        oldStatus,
                        updated.getStatus(),
                        updated.getService().getName()
                );
                notificationServer.broadcastNotification(notification);
                System.out.println("📢 Socket notification sent: Appointment status changed");
            }
        } catch (Exception e) {
            System.err.println("⚠️ Failed to send socket notification: " + e.getMessage());
            // Don't fail the update if notification fails
        }

        return convertToDTO(updated);
    }

    public void deleteAppointment(Long id) {
        if (!appointmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Appointment", id);
        }

        // Get appointment details before deletion for notification
        Appointment appointment = appointmentRepository.findById(id).orElse(null);

        appointmentRepository.deleteById(id);

        // NEW: Send notification via socket server
        try {
            if (appointment != null) {
                String notification = String.format(
                        "APPOINTMENT_DELETED|ID:%d|CLIENT:%s|SERVICE:%s",
                        appointment.getId(),
                        appointment.getClient().getFullName(),
                        appointment.getService().getName()
                );
                notificationServer.broadcastNotification(notification);
                System.out.println("📢 Socket notification sent: Appointment deleted");
            }
        } catch (Exception e) {
            System.err.println("⚠️ Failed to send socket notification: " + e.getMessage());
        }
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