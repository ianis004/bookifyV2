package com.bookify.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 *Data Transfer Object for BusinessSettings
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessSettingsDTO {

    private Long id;

    @NotBlank(message = "Business name is required")
    private String businessName;

    @NotNull(message = "Opening time is required")
    private LocalTime openingTime;

    @NotNull(message = "Closing time is required")
    private LocalTime closingTime;

    @NotNull(message = "Slot duration is required")
    @Positive(message = "Slot duration must be positive")
    private Integer slotDurationMinutes;

    @NotNull(message = "Online booking preference is required")
    private Boolean acceptOnlineBookings;

    private String contactEmail;

    private String contactPhone;

    private String address;
}
