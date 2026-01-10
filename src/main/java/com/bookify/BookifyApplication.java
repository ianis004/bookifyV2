package com.bookify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class BookifyApplication {

    public static void main(String[] args) {
        // start: java -jar target/bookify-1.0.0.jar --spring.profiles.active=dev
        // or: java -jar target/bookify-1.0.0.jar --spring.profiles.active=prod
        SpringApplication.run(BookifyApplication.class, args);
    }
}