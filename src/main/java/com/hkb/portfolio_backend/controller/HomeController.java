package com.hkb.portfolio_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HomeController {
    @GetMapping("/")
    public Map<String, String> home(){
        return Map.of("message", "Welcome to Your Java Full-Stack Portfolio Backend!\n",
                "status", "Running on Spring Boot 3.2 with PostgreSQL\n",
                "endpoints", "/api/projects (GET/POST), /api/contact (POST), /swagger-ui.html\n",
                "next", "Proceed to JWT auth in Part");
    }
}
