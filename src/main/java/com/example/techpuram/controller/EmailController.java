package com.example.techpuram.controller;

import com.example.techpuram.Entity.dto.EmailDTO;
import com.example.techpuram.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
@CrossOrigin(origins = "http://localhost:5173")  // Allow CORS for the frontend on port 5173
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
}
 
