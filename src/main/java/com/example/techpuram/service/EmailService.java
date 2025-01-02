package com.example.techpuram.service;

import com.example.techpuram.Entity.dto.EmailDTO;
import com.example.techpuram.Entity.dto.Template;
import jakarta.mail.*;
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
    private TemplateService templateService;  // Inject TemplateService

    public void sendEmail(EmailDTO emailDTO) {
        try {
            // Fetch template by ID
            Template template = templateService.getTemplateById(emailDTO.getTemplateId())
                    .orElseThrow(() -> new Exception("Template not found"));

            // If the template has a subject and body, use them
            String subject = emailDTO.getSubject() != null ? emailDTO.getSubject() : template.getSubject();
            String body = emailDTO.getBody() != null ? emailDTO.getBody() : template.getBody();

            var message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(emailDTO.getFromAddress());
            helper.setTo(emailDTO.getToAddress());
            helper.setSubject(subject);
            helper.setText(body);

            if (emailDTO.getCcAddress() != null && !emailDTO.getCcAddress().isEmpty()) {
                helper.setCc(emailDTO.getCcAddress());
            }

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



