package com.hkb.portfolio_backend.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {


    @GetMapping("/stats")
    public String getAdminStats() {
        // only ADMIN can call this
        return "sensitive stats";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/promote/{userId}")
    public String promoteToAdmin(@PathVariable Long userId) {
        // implement user role update logic in service
        return "promoted user " + userId;
    }
}