package com.bookify.config;

import com.bookify.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ServiceInitializer - Creates default services on application startup
 */
@Component
@Order(2) // Run after DataInitializer
public class ServiceInitializer implements CommandLineRunner {

    @Autowired
    private ServiceRepository serviceRepository;

    @Override
    public void run(String... args) throws Exception {
        // Check if services already exist
        if (serviceRepository.count() == 0) {

            System.out.println("🔄 Creating default services...");

            // Create Service 1: Haircut
            com.bookify.entity.Service haircut = com.bookify.entity.Service.builder()
                    .name("Haircut & Styling")
                    .description("Professional haircut and styling service")
                    .durationMinutes(30)
                    .price(new BigDecimal("25.00"))
                    .active(true)
                    .build();
            serviceRepository.save(haircut);

            // Create Service 2: Hair Coloring
            com.bookify.entity.Service coloring = com.bookify.entity.Service.builder()
                    .name("Hair Coloring")
                    .description("Full hair coloring service with premium products")
                    .durationMinutes(90)
                    .price(new BigDecimal("75.00"))
                    .active(true)
                    .build();
            serviceRepository.save(coloring);

            // Create Service 3: Manicure
            com.bookify.entity.Service manicure = com.bookify.entity.Service.builder()
                    .name("Manicure")
                    .description("Complete nail care and polish application")
                    .durationMinutes(45)
                    .price(new BigDecimal("30.00"))
                    .active(true)
                    .build();
            serviceRepository.save(manicure);

            // Create Service 4: Pedicure
            com.bookify.entity.Service pedicure = com.bookify.entity.Service.builder()
                    .name("Pedicure")
                    .description("Relaxing foot spa and nail care")
                    .durationMinutes(60)
                    .price(new BigDecimal("40.00"))
                    .active(true)
                    .build();
            serviceRepository.save(pedicure);

            // Create Service 5: Massage
            com.bookify.entity.Service massage = com.bookify.entity.Service.builder()
                    .name("Relaxing Massage")
                    .description("Full body relaxing massage therapy")
                    .durationMinutes(60)
                    .price(new BigDecimal("80.00"))
                    .active(true)
                    .build();
            serviceRepository.save(massage);

            // Create Service 6: Facial Treatment
            com.bookify.entity.Service facial = com.bookify.entity.Service.builder()
                    .name("Facial Treatment")
                    .description("Deep cleansing and rejuvenating facial")
                    .durationMinutes(50)
                    .price(new BigDecimal("65.00"))
                    .active(true)
                    .build();
            serviceRepository.save(facial);

            System.out.println("✅ Default services created successfully!");
            System.out.println("   💇 Haircut & Styling - $25 (30 min)");
            System.out.println("   🎨 Hair Coloring - $75 (90 min)");
            System.out.println("   💅 Manicure - $30 (45 min)");
            System.out.println("   🦶 Pedicure - $40 (60 min)");
            System.out.println("   💆 Relaxing Massage - $80 (60 min)");
            System.out.println("   ✨ Facial Treatment - $65 (50 min)");
        } else {
            System.out.println("ℹ️  Services already exist. Skipping initialization.");
        }
    }
}