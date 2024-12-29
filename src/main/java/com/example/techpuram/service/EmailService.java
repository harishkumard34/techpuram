package com.example.techpuram.service;

import com.example.techpuram.Entity.dto.EmailDTO;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    // Create logger instance for logging
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    // Use JavaMailSender for sending emails
    @Autowired
    private JavaMailSender javaMailSender;

    // Method to send an email using EmailDTO
    public void sendEmail(EmailDTO emailDTO) {
        try {
            // Create a MimeMessage to handle more advanced email content
            var message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            // Set the values from EmailDTO
            helper.setFrom(emailDTO.getFrom());

            // Convert to InternetAddress if not already
            InternetAddress[] toAddresses = new InternetAddress[1];  // Assuming there is one recipient
            toAddresses[0] = new InternetAddress(emailDTO.getTo());
            helper.setTo(toAddresses);

            helper.setSubject(emailDTO.getSubject());
            helper.setText(emailDTO.getBody());

            // Set CC if provided (CC is now a String, so we handle it directly)
            if (emailDTO.getCc() != null && !emailDTO.getCc().isEmpty()) {
                InternetAddress[] ccAddresses = new InternetAddress[1];
                ccAddresses[0] = new InternetAddress(emailDTO.getCc()); // Handle as a single email string
                helper.setCc(ccAddresses);
            }

            // Send the email
            javaMailSender.send(message);
            logger.info("Email Sent Successfully!");

        } catch (MailException e) {
            logger.error("Error in sending email: {}", e.getMessage(), e);
        } catch (MessagingException e) {
            logger.error("Error in setting email properties: {}", e.getMessage(), e);
        }
    }

    // Method to receive and print email details (from, to, subject, body, cc)
   
}
