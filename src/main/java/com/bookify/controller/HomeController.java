package com.bookify.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String register() {
        return "auth/register";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "client/dashboard";
    }

    @GetMapping("/services")
    public String services() {
        return "client/services";
    }

    @GetMapping("/booking")
    public String booking() {
        return "client/booking";
    }

    @GetMapping("/my-appointments")
    public String myAppointments() {
        return "client/my-appointments";
    }

    @GetMapping("/staff/appointments")
    public String staffAppointments() {
        return "staff/appointments";
    }

    @GetMapping("/logout")
    public String logout() {
        return "auth/logout";
    }
}
