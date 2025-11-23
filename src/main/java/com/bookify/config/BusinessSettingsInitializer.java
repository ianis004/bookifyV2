package com.bookify.config;

import com.bookify.entity.BusinessSettings;
import com.bookify.repository.BusinessSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

/**
 * Creates default business settings on startup
 */
@Component
@Order(3)
public class BusinessSettingsInitializer implements CommandLineRunner {

    @Autowired
    private BusinessSettingsRepository businessSettingsRepository;

    @Override
    public void run(String... args) throws Exception {
        if (businessSettingsRepository.count() == 0) {

            System.out.println("🔄 Creating business settings...");

            BusinessSettings settings = BusinessSettings.builder()
                    .businessName("Bookify Salon & Spa")
                    .openingTime(LocalTime.of(9, 0))      // 9:00 AM
                    .closingTime(LocalTime.of(18, 0))     // 6:00 PM
                    .slotDurationMinutes(30)
                    .acceptOnlineBookings(true)           // if it's put to false then the client needs to contact the business himself
                    .contactEmail("info@bookify.com")
                    .contactPhone("+40-123-456-789")
                    .address("Str. Victoriei 1, Timișoara, Romania")
                    .build();

            businessSettingsRepository.save(settings);

            System.out.println("✅ Business settings created successfully!");
        } else {
            System.out.println("ℹ️  Business settings already exist. Skipping initialization.");
        }
    }
}
