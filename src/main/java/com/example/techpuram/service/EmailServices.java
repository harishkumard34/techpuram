package com.example.techpuram.service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMultipart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.techpuram.Entity.dto.Email;
import com.example.techpuram.Entity.dto.EmailRepository;

import org.eclipse.angus.mail.imap.IMAPFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

@Service
public class EmailServices {
    private static final Logger logger = LoggerFactory.getLogger(EmailServices.class);

    @Autowired
    private EmailRepository emailRepository;

    @Scheduled(fixedRate = 5000) // Run every 5 seconds
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
            IMAPFolder inbox = (IMAPFolder) store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            // Fetch the last processed UID from the database or initialize with 0
            long lastProcessedUID = emailRepository.findMaxUid().orElse(0L); // Assuming a custom query

            // Fetch only emails with UID greater than the last processed UID
            Message[] messages = inbox.getMessagesByUID(lastProcessedUID + 1, Long.MAX_VALUE);

            for (Message message : messages) {
                long uid = inbox.getUID(message);

                // Create and populate the Email entity
                Email email = new Email();
                email.setUid(String.valueOf(uid));
                email.setFromAddress(((InternetAddress) message.getFrom()[0]).getAddress());
                email.setToAddress(((InternetAddress) message.getRecipients(Message.RecipientType.TO)[0]).getAddress());
                email.setSubject(message.getSubject());
                email.setBody(extractEmailBody(message));

                if (message.getRecipients(Message.RecipientType.CC) != null) {
                    email.setCcAddress(((InternetAddress) message.getRecipients(Message.RecipientType.CC)[0]).getAddress());
                }

                // Save the email
                emailRepository.save(email);
                logger.info("New email saved successfully: {}", email);
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
            return textContent;
        } else if (content instanceof MimeMultipart mimeMultipart) {
            return getTextFromMimeMultipart(mimeMultipart);
        }
        return "Unsupported email content type";
    }

    private String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws MessagingException, IOException {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < mimeMultipart.getCount(); i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result.append(bodyPart.getContent());
            } else if (bodyPart.isMimeType("text/html")) {
                result.append(bodyPart.getContent());
            } else if (bodyPart.getContent() instanceof MimeMultipart nestedMultipart) {
                result.append(getTextFromMimeMultipart(nestedMultipart));
            }
        }
        return result.toString();
    }
}

