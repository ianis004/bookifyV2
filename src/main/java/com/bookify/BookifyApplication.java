package com.bookify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class BookifyApplication {

    public static void main(String[] args) {
        // Support for configuration files and program arguments
        // Usage: java -jar bookify.jar --spring.profiles.active=dev
        // OR: java -jar bookify.jar --spring.profiles.active=prod
        SpringApplication.run(BookifyApplication.class, args);
    }
}