package com.example.techpuram.service;

import com.example.techpuram.Entity.dto.EmailDTO;
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

    public void sendEmail(EmailDTO emailDTO) {
        try {
            var message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(emailDTO.getFromAddress());
            helper.setTo(emailDTO.getToAddress());
            helper.setSubject(emailDTO.getSubject());
            helper.setText(emailDTO.getBody());

            if (emailDTO.getCcAddress() != null && !emailDTO.getCcAddress().isEmpty()) {
                helper.setCc(emailDTO.getCcAddress());
            }

            javaMailSender.send(message);
            logger.info("Email Sent Successfully!");

        } catch (MailException e) {
            logger.error("Error in sending email: {}", e.getMessage(), e);
        } catch (MessagingException e) {
            logger.error("Error in setting email properties: {}", e.getMessage(), e);
        }
    }
}

