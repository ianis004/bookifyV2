package com.bookify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class BookifyApplication {

    public static void main(String[] args) {
        // start: java -jar target/bookify-1.0.0.jar --spring.profiles.active=dev
        // or if you want to use MySQL : java -jar target/bookify-1.0.0.jar --spring.profiles.active=prod
        // for in-memory H2: jdbc:h2:mem:bookifydb
        // for file based H2: jdbc:h2:file:./data/bookifydb
        // for H2 file based with password : jdbc:h2:file:./data/bookify-dev;CIPHER=AES (dev password)
        // to create a new jar file run these : Remove-Item .\data\bookify-dev.mv.db -Force -ErrorAction SilentlyContinue
        //                                      Remove-Item .\data\bookify-dev.trace.db -Force -ErrorAction SilentlyContinue
        //                                      mvn clean package
        SpringApplication.run(BookifyApplication.class, args);
    }
}