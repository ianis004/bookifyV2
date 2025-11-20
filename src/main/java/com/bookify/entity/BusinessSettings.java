package com.bookify.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

/**
 *System configuration
 */
@Entity
@Table(name = "business_settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String businessName;

    @Column(nullable = false)
    private LocalTime openingTime;

    @Column(nullable = false)
    private LocalTime closingTime;

    @Column(nullable = false)
    private Integer slotDurationMinutes;

    @Column(nullable = false)
    private Boolean acceptOnlineBookings;

    private String contactEmail;

    private String contactPhone;

    @Column(length = 500)
    private String address;
}