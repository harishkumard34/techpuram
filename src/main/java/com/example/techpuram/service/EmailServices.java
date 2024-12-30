package com.example.techpuram.service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMultipart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.techpuram.Entity.dto.Email;
import com.example.techpuram.Entity.dto.EmailRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

@Service
public class EmailServices {
    private static final Logger logger = LoggerFactory.getLogger(EmailServices.class);

    @Autowired
    private EmailRepository emailRepository;

    public void fetchAndSaveEmails() {
        try {
            // IMAP Server properties
            Properties properties = new Properties();
            properties.put("mail.imap.host", "imap.hostinger.com");
            properties.put("mail.imap.port", "993");
            properties.put("mail.imap.ssl.enable", "true");
            properties.put("mail.imap.auth", "true");

            // Session and Store
            Session session = Session.getInstance(properties);
            Store store = session.getStore("imap");
            store.connect("imap.hostinger.com", "harishkumar.d@techpuram.com", "n#U~5|9D");

            // Open the INBOX folder
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            // Fetch messages
            Message[] messages = inbox.getMessages();
            for (Message message : messages) {
                Email email = new Email();
                email.setFromAddress(((InternetAddress) message.getFrom()[0]).getAddress());
                email.setToAddress(((InternetAddress) message.getRecipients(Message.RecipientType.TO)[0]).getAddress());
                email.setSubject(message.getSubject());
                email.setBody(extractEmailBody(message)); // Extract the email body properly

                if (message.getRecipients(Message.RecipientType.CC) != null) {
                    email.setCcAddress(((InternetAddress) message.getRecipients(Message.RecipientType.CC)[0]).getAddress());
                }

                // Save to database
                emailRepository.save(email);

                // Log success message using the logger
                logger.info("Email saved successfully: {}", email);
            }

            inbox.close(false);
            store.close();
        } catch (Exception e) {
            logger.error("Error while fetching and saving emails", e);
        }
    }

    private String extractEmailBody(Message message) throws MessagingException, IOException {
        Object content = message.getContent();
        if (content instanceof String textContent) {
            return textContent; // For simple text emails
        } else if (content instanceof MimeMultipart mimeMultipart) {
            return getTextFromMimeMultipart(mimeMultipart); // Handle multipart emails
        }
        return "Unsupported email content type";
    }

    private String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws MessagingException, IOException {
        StringBuilder result = new StringBuilder();
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result.append(bodyPart.getContent());
            } else if (bodyPart.isMimeType("text/html")) {
                // Optional: Choose to prefer plain text or HTML
                result.append(bodyPart.getContent());
            } else if (bodyPart.getContent() instanceof MimeMultipart nestedMultipart) {
                result.append(getTextFromMimeMultipart(nestedMultipart));
            }
        }
        return result.toString();
    }
}

