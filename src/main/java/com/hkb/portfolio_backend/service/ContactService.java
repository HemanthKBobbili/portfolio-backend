package com.hkb.portfolio_backend.service;

import com.hkb.portfolio_backend.dto.ContactDto;
import com.hkb.portfolio_backend.entity.Contact;
import com.hkb.portfolio_backend.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;

    public ContactDto submitContact(ContactDto dto) {
        Contact contact = new Contact();
        contact.setName(dto.getName());
        contact.setEmail(dto.getEmail());
        contact.setMessage(dto.getMessage());
        contact = contactRepository.save(contact);
        return dto;  // Return input (or enhanced response)
    }

    // Get all contacts (for admin view)
    public List<Contact> getAllContacts() {
        return contactRepository.findAll();
    }
}
