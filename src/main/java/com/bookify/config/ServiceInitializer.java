package com.bookify.config;

import com.bookify.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 *Creates default services on application startup
 */
@Component
@Order(2) // creates services after users
public class ServiceInitializer implements CommandLineRunner {

    @Autowired
    private ServiceRepository serviceRepository;

    @Override
    public void run(String... args) throws Exception {
        if (serviceRepository.count() == 0) {

            System.out.println("🔄 Creating default services...");

            com.bookify.entity.Service haircut = com.bookify.entity.Service.builder()
                    .name("Haircut & Styling")
                    .description("Professional haircut and styling service")
                    .durationMinutes(30)
                    .price(new BigDecimal("60.00"))
                    .active(true)
                    .build();
            serviceRepository.save(haircut);

            com.bookify.entity.Service coloring = com.bookify.entity.Service.builder()
                    .name("Hair Coloring")
                    .description("Full hair coloring service with premium products")
                    .durationMinutes(90)
                    .price(new BigDecimal("140.00"))
                    .active(true)
                    .build();
            serviceRepository.save(coloring);

            com.bookify.entity.Service manicure = com.bookify.entity.Service.builder()
                    .name("Manicure")
                    .description("Complete nail care and polish application")
                    .durationMinutes(45)
                    .price(new BigDecimal("150.00"))
                    .active(true)
                    .build();
            serviceRepository.save(manicure);

            com.bookify.entity.Service pedicure = com.bookify.entity.Service.builder()
                    .name("Pedicure")
                    .description("Relaxing foot spa and nail care")
                    .durationMinutes(60)
                    .price(new BigDecimal("120.00"))
                    .active(true)
                    .build();
            serviceRepository.save(pedicure);

            com.bookify.entity.Service massage = com.bookify.entity.Service.builder()
                    .name("Relaxing Massage")
                    .description("Full body relaxing massage therapy")
                    .durationMinutes(60)
                    .price(new BigDecimal("80.00"))
                    .active(true)
                    .build();
            serviceRepository.save(massage);

            com.bookify.entity.Service facial = com.bookify.entity.Service.builder()
                    .name("Facial Treatment")
                    .description("Deep cleansing and rejuvenating facial")
                    .durationMinutes(50)
                    .price(new BigDecimal("300.00"))
                    .active(true)
                    .build();
            serviceRepository.save(facial);

            System.out.println("✅ Default services created successfully!");
            System.out.println("   💇 Haircut & Styling - 60 lei (30 min)");
            System.out.println("   🎨 Hair Coloring - 140 lei (90 min)");
            System.out.println("   💅 Manicure - 150 lei (45 min)");
            System.out.println("   🦶 Pedicure - 120 lei (60 min)");
            System.out.println("   💆 Relaxing Massage - 80 lei (60 min)");
            System.out.println("   ✨ Facial Treatment - 300 lei (50 min)");
        } else {
            System.out.println("ℹ️  Services already exist. Skipping initialization.");
        }
    }
}