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
 *Creates default users on application startup
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
        if (userRepository.count() == 0) {

            System.out.println("🎎 Creating default users...");

            User admin = User.builder()
                    .username("admin")
                    .email("admin@bookify.com")
                    .password(passwordEncoder.encode("password"))
                    .fullName("Admin User")
                    .phone("+40 0734558923")
                    .role(Role.ADMIN)
                    .status(UserStatus.ACTIVE)
                    .build();
            userRepository.save(admin);

            User staff = User.builder()
                    .username("staff")
                    .email("staff@bookify.com")
                    .password(passwordEncoder.encode("password"))
                    .fullName("Staff Member")
                    .phone("+40 0723338457")
                    .role(Role.STAFF)
                    .status(UserStatus.ACTIVE)
                    .build();
            userRepository.save(staff);

            User client1 = User.builder()
                    .username("client1")
                    .email("client1@example.com")
                    .password(passwordEncoder.encode("password"))
                    .fullName("Dan Daniel")
                    .phone("+40 0723468487")
                    .role(Role.CLIENT)
                    .status(UserStatus.ACTIVE)
                    .build();
            userRepository.save(client1);

            User client2 = User.builder()
                    .username("client2")
                    .email("client2@example.com")
                    .password(passwordEncoder.encode("password"))
                    .fullName("Maria Elena")
                    .phone("++40 0793854576")
                    .role(Role.CLIENT)
                    .status(UserStatus.ACTIVE)
                    .build();
            userRepository.save(client2);

            System.out.println("✅ Default users created successfully");
        } else {
            System.out.println("✨ Users already exist, Skipping initialization.");
        }
    }
}
