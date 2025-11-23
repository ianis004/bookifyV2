package com.bookify.repository;

import com.bookify.entity.Appointment;
import com.bookify.entity.User;
import com.bookify.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data access for appointment entity
 */
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByClient(User client);

    List<Appointment> findByClientId(Long clientId);

    List<Appointment> findByClientUsername(String username);

    List<Appointment> findByStatus(AppointmentStatus status);

    List<Appointment> findByAppointmentDateTimeBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT a FROM Appointment a WHERE a.service.id = :serviceId " +
            "AND a.appointmentDateTime BETWEEN :start AND :end " +
            "AND a.status != 'CANCELLED'")
    List<Appointment> findServiceAppointmentsInRange(
            @Param("serviceId") Long serviceId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE " +
            "a.service.id = :serviceId AND " +
            "a.appointmentDateTime = :dateTime AND " +
            "a.status != 'CANCELLED'")
    boolean existsConflictingAppointment(
            @Param("serviceId") Long serviceId,
            @Param("dateTime") LocalDateTime dateTime
    );
}