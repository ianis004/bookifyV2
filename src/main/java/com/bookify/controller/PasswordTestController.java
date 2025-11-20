
package com.bookify.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PasswordTestController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/generate-password")
    public String generatePassword(@RequestParam String password) {
        return passwordEncoder.encode(password);
    }
}

