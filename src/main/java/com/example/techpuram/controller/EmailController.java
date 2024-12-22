package com.example.techpuram.controller;

import com.example.techpuram.Entity.dto.EmailDTO;
import com.example.techpuram.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    // POST endpoint to send an email
    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestBody EmailDTO emailDTO) {
        try {
            emailService.sendEmail(emailDTO);
            return ResponseEntity.status(HttpStatus.OK).body("Email sent successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error sending email: " + e.getMessage());
        }
    }

    // GET endpoint to receive emails
    @GetMapping("/receive")
    public ResponseEntity<List<EmailDTO>> receiveEmails() {
        try {
            List<EmailDTO> emails = emailService.receiveEmail();
            if (emails != null && !emails.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK).body(emails);
            } else {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(null);  // Return 500 if there is an error
        }
    }
}
