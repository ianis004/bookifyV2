package com.bookify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class BookifyApplication {

    public static void main(String[] args) {
        // start: java -jar bookify.jar --spring.profiles.active=dev
        // or: java -jar bookify.jar --spring.profiles.active=prod
        SpringApplication.run(BookifyApplication.class, args);
    }
}