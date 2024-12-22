package com.example.techpuram.service;


import org.springframework.stereotype.Service;

import com.example.techpuram.Entity.Email;
import com.example.techpuram.repository.EmailRepository;

@Service

public class EmailServices {
    private final EmailRepository emailRepository;
    
    public EmailServices(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    public void saveEmail(Email email) {
        emailRepository.save(email);  // This will save the email details to the PostgreSQL database
    }

}
