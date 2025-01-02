package com.example.techpuram.service;

import com.example.techpuram.Entity.dto.EmailDTO;
import com.example.techpuram.Entity.dto.Template;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender javaMailSender; // Spring-provided email utility for sending messages

    @Autowired
    private TemplateService templateService; // Service to fetch email templates from the database

    /**
     * Method to send an email based on provided EmailDTO and template information.
     * 
     * @param emailDTO Data transfer object containing email details
     */
    public void sendEmail(EmailDTO emailDTO) {
        try {
            // Fetch template by ID using the TemplateService
            Template template = templateService.getTemplateById(emailDTO.getTemplateId())
                    .orElseThrow(() -> new Exception("Template not found")); // Throw exception if template doesn't exist

            // Determine subject and body: use EmailDTO values if provided, otherwise fallback to template values
            String subject = emailDTO.getSubject() != null ? emailDTO.getSubject() : template.getSubject();
            String body = emailDTO.getBody() != null ? emailDTO.getBody() : template.getBody();

            // Create MIME message object to compose the email
            var message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true); // Helper to set email properties

            // Set basic email properties
            helper.setFrom(emailDTO.getFromAddress()); // Sender's address
            helper.setTo(emailDTO.getToAddress()); // Recipient's address
            helper.setSubject(subject); // Email subject
            helper.setText(body); // Email body content

            // Optional: Set CC addresses if provided in the EmailDTO
            if (emailDTO.getCcAddress() != null && !emailDTO.getCcAddress().isEmpty()) {
                helper.setCc(emailDTO.getCcAddress());
            }

            // Send the email using JavaMailSender
            javaMailSender.send(message);
            logger.info("Email Sent Successfully!"); // Log success message

        } catch (MailException e) {
            // Log error related to sending the email
            logger.error("Error in sending email: {}", e.getMessage(), e);
        } catch (MessagingException e) {
            // Log error related to setting up the email properties
            logger.error("Error in setting email properties: {}", e.getMessage(), e);
        } catch (Exception e) {
            // Catch-all for other exceptions, e.g., Template not found
            logger.error("Error: {}", e.getMessage(), e);
        }
    }
}
