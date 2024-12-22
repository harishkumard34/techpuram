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

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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
    public List<EmailDTO> receiveEmail() {
        List<EmailDTO> emailDTOList = new ArrayList<>(); // Initialize the list to hold EmailDTO objects

        try {
            // Set IMAP properties
            Properties props = new Properties();
            props.put("mail.imap.host", "imap.gmail.com");
            props.put("mail.imap.port", "993");
            props.put("mail.imap.ssl.enable", "true");

            // Create a mail session
            Session emailSession = Session.getDefaultInstance(props);
            Store store = emailSession.getStore("imap");
            store.connect("imap.gmail.com", "your-email@gmail.com", "your-email-password");

            // Open inbox folder
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);

            // Fetch and print email details
            Message[] messages = folder.getMessages();
            logger.info("Number of messages: {}", messages.length);

            for (Message message : messages) {
                // Extract email details
                String from = ((InternetAddress) message.getFrom()[0]).getAddress();
                String to = ((InternetAddress) message.getAllRecipients()[0]).getAddress();
                String subject = message.getSubject();
                String body = message.getContent().toString();
                String cc = "";

                if (message.getRecipients(Message.RecipientType.CC) != null) {
                    // Join the CC recipients into a comma-separated string
                    cc = String.join(", ", (CharSequence[]) message.getRecipients(Message.RecipientType.CC));
                }

                // Create a new EmailDTO object and set its fields
                EmailDTO emailDTO = new EmailDTO();
                emailDTO.setFrom(from);
                emailDTO.setTo(to);
                emailDTO.setSubject(subject);
                emailDTO.setBody(body);
                emailDTO.setCc(cc); // Set the cc as a string

                // Add the EmailDTO object to the list
                emailDTOList.add(emailDTO);

                // Log the email details (optional)
                logger.info("From: {}", from);
                logger.info("To: {}", to);
                logger.info("Subject: {}", subject);
                logger.info("Body: {}", body);
                logger.info("CC: {}", cc);
            }

            // Close folder and store
            folder.close(false);
            store.close();

        } catch (Exception e) {
            logger.error("Error in receiving emails: {}", e.getMessage(), e);
        }

        // Return the list of EmailDTO objects
        return emailDTOList;
    }
}
