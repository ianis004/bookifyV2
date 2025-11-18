package com.bookify.exception;

/**
 * Custom Exception: Appointment Conflict
 * Thrown when there's a scheduling conflict
 */
public class AppointmentConflictException extends RuntimeException {

    public AppointmentConflictException(String message) {
        super(message);
    }

    public AppointmentConflictException() {
        super("The requested time slot is already booked. Please choose another time.");
    }
}