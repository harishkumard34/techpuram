package com.example.techpuram.service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.techpuram.Entity.dto.Email;
import com.example.techpuram.Entity.dto.EmailRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

@Service
public class EmailServices {
    private static final Logger logger = LoggerFactory.getLogger(EmailServices.class); // Logger initialization

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
                email.setBody(message.getContent().toString());

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
            logger.error("Error while fetching and saving emails", e); // Log errors if any
        }
    }
}
