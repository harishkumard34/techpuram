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
    private JavaMailSender javaMailSender;

    @Autowired
    private TemplateService templateService;

    /**
     * Method to send an email based on provided EmailDTO and template information.
     *
     * @param emailDTO Data transfer object containing email details
     */
    public void sendEmail(EmailDTO emailDTO) {
        try {
            // Fetch template by ID using the TemplateService
            Template template = templateService.getTemplateById(emailDTO.getTemplateId())
                    .orElseThrow(() -> new Exception("Template not found"));

            // Determine subject and body: use EmailDTO values if provided, otherwise fallback to template values
            String subject = emailDTO.getSubject() != null ? emailDTO.getSubject() : template.getSubject();
            String body = emailDTO.getBody() != null ? emailDTO.getBody() : template.getBody();

            // Create MIME message object to compose the email
            var message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            // Set basic email properties
            helper.setFrom(emailDTO.getFromAddress());
            helper.setTo(emailDTO.getToAddress());
            helper.setSubject(subject);
            helper.setText(body, true); // Set email body as HTML

            // Optional: Set CC addresses if provided in the EmailDTO
            if (emailDTO.getCcAddress() != null && !emailDTO.getCcAddress().isEmpty()) {
                helper.setCc(emailDTO.getCcAddress());
            }

            // Optional: Set BCC addresses if provided in the EmailDTO
            if (emailDTO.getBccAddress() != null && !emailDTO.getBccAddress().isEmpty()) {
                helper.setBcc(emailDTO.getBccAddress());
            }

            // Send the email using JavaMailSender
            javaMailSender.send(message);
            logger.info("Email Sent Successfully!");

        } catch (MailException e) {
            logger.error("Error in sending email: {}", e.getMessage(), e);
        } catch (MessagingException e) {
            logger.error("Error in setting email properties: {}", e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error: {}", e.getMessage(), e);
        }
    }
}