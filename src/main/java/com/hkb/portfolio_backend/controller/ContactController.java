package com.hkb.portfolio_backend.controller;

import com.hkb.portfolio_backend.dto.ContactDto;
import com.hkb.portfolio_backend.entity.Contact;
import com.hkb.portfolio_backend.service.ContactService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contact")
@CrossOrigin(origins = "http://localhost:4200")
public class ContactController {

    @Autowired
    private ContactService contactService;

    // POST /api/contact - Submit form
    @PostMapping
    public ResponseEntity<String> submitContact(@Valid @RequestBody ContactDto dto) {
        contactService.submitContact(dto);
        return ResponseEntity.ok("Message submitted successfully!");
    }

    // GET /api/contact - List all (admin only later)
    @GetMapping
    public List<Contact> getAllContacts() {  // Import from entity
        return contactService.getAllContacts();
    }
}
