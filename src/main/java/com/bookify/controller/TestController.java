package com.bookify.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * TestController - Simple test endpoints
 */
@RestController
public class TestController {

    @GetMapping("/")
    public Map<String, String> home() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Welcome to Bookify API!");
        response.put("status", "running");
        response.put("h2Console", "http://localhost:8080/h2-console");
        response.put("apiDocs", "http://localhost:8080/api/services");
        return response;
    }

    @GetMapping("/api/status")
    public Map<String, String> status() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "OK");
        response.put("application", "Bookify");
        response.put("version", "1.0.0");
        return response;
    }
}