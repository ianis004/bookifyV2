package com.bookify.config;

import com.bookify.entity.User;
import com.bookify.enums.Role;
import com.bookify.enums.UserStatus;
import com.bookify.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * DataInitializer - Creates default users on application startup
 */
@Component
@Order(1)
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Check if users already exist
        if (userRepository.count() == 0) {

            System.out.println("🔄 Creating default users...");

            // Create Admin
            User admin = User.builder()
                    .username("admin")
                    .email("admin@bookify.com")
                    .password(passwordEncoder.encode("password"))
                    .fullName("Admin User")
                    .phone("+1234567890")
                    .role(Role.ADMIN)
                    .status(UserStatus.ACTIVE)
                    .build();
            userRepository.save(admin);

            // Create Staff
            User staff = User.builder()
                    .username("staff")
                    .email("staff@bookify.com")
                    .password(passwordEncoder.encode("password"))
                    .fullName("Staff Member")
                    .phone("+1234567891")
                    .role(Role.STAFF)
                    .status(UserStatus.ACTIVE)
                    .build();
            userRepository.save(staff);

            // Create Client 1
            User client1 = User.builder()
                    .username("client1")
                    .email("client1@example.com")
                    .password(passwordEncoder.encode("password"))
                    .fullName("John Doe")
                    .phone("+1234567892")
                    .role(Role.CLIENT)
                    .status(UserStatus.ACTIVE)
                    .build();
            userRepository.save(client1);

            // Create Client 2
            User client2 = User.builder()
                    .username("client2")
                    .email("client2@example.com")
                    .password(passwordEncoder.encode("password"))
                    .fullName("Jane Smith")
                    .phone("+1234567893")
                    .role(Role.CLIENT)
                    .status(UserStatus.ACTIVE)
                    .build();
            userRepository.save(client2);

            System.out.println("✅ Default users created successfully!");
            System.out.println("   👤 Admin: admin / password");
            System.out.println("   👤 Staff: staff / password");
            System.out.println("   👤 Client1: client1 / password");
            System.out.println("   👤 Client2: client2 / password");
        } else {
            System.out.println("ℹ️  Users already exist. Skipping initialization.");
        }
    }
}
