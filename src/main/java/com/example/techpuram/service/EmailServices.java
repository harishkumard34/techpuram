package com.example.techpuram.service;

import jakarta.mail.*;
import jakarta.mail.event.MessageCountAdapter;
import jakarta.mail.event.MessageCountEvent;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMultipart;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Value("${spring.mail.imap.host}")
    private String imapHost;

    @Value("${spring.mail.imap.port}")
    private int imapPort;

    @Value("${spring.mail.imap.username}")
    private String imapUsername;

    @Value("${spring.mail.imap.password}")
    private String imapPassword;

    private volatile boolean running = true;

    public void connectAndListenForEmails() {
        try {
            // IMAP Server properties from application.properties
            Properties properties = new Properties();
            properties.put("mail.imap.host", imapHost); // Use injected property
            properties.put("mail.imap.port", String.valueOf(imapPort)); // Use injected property
            properties.put("mail.imap.ssl.enable", "true");
            properties.put("mail.imap.auth", "true");

            // Create a mail session with the specified properties
            Session session = Session.getInstance(properties);

            // Get an IMAP store to connect to the server
            Store store = session.getStore("imap");
            store.connect(imapHost, imapUsername, imapPassword); // Use injected credentials

            // Open the INBOX folder to fetch emails
            IMAPFolder inbox = (IMAPFolder) store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY); // Open folder in read-only mode to prevent accidental changes

            // Add a listener to monitor new emails arriving in the inbox
            inbox.addMessageCountListener(new MessageCountAdapter() {
                @Override
                public void messagesAdded(MessageCountEvent event) {
                    Message[] messages = event.getMessages();
                    for (Message message : messages) {
                        try {
                            processAndSaveEmail(inbox, message); // Save email details to the database
                        } catch (Exception e) {
                            logger.error("Error processing new email", e);
                        }
                    }
                }
            });

            // Enter a continuous loop to listen for new messages using the IDLE command
            while (running) {
                inbox.idle(); // Keeps the connection open and waits for server notifications
            }

            inbox.close(false); // Close without expunging messages
            store.close(); // Close the store connection
        } catch (Exception e) {
            logger.error("Error while connecting and listening for emails", e); // Log connection errors
        }
    }

    private void processAndSaveEmail(IMAPFolder inbox, Message message) throws MessagingException, IOException {
        long uid = inbox.getUID(message); // Retrieve the unique identifier (UID) of the email

        // Skip if the email UID already exists in the database
        if (emailRepository.existsByUid(String.valueOf(uid))) {
            return;
        }

        // Create a new Email entity and populate it with message details
        Email email = new Email();
        email.setUid(String.valueOf(uid)); // Set the unique ID
        email.setFromAddress(((InternetAddress) message.getFrom()[0]).getAddress()); // Sender's address
        email.setToAddress(((InternetAddress) message.getRecipients(Message.RecipientType.TO)[0]).getAddress()); // Recipient's address
        email.setSubject(message.getSubject()); // Subject of the email
        email.setBody(extractEmailBody(message)); // Extract the email body content

        // Set CC address if it exists
        if (message.getRecipients(Message.RecipientType.CC) != null) {
            email.setCcAddress(((InternetAddress) message.getRecipients(Message.RecipientType.CC)[0]).getAddress());
        }

        // Save the email entity to the database
        emailRepository.save(email);
        logger.info("New email saved successfully: {}", email); // Log success
    }

    private String extractEmailBody(Message message) throws MessagingException, IOException {
        Object content = message.getContent(); // Get the content of the message
        if (content instanceof String textContent) {
            return textContent; // If plain text, return directly
        } else if (content instanceof MimeMultipart mimeMultipart) {
            return getTextFromMimeMultipart(mimeMultipart); // Handle multipart messages recursively
        }
        return "Unsupported email content type"; // For unsupported content types
    }

    private String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws MessagingException, IOException {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < mimeMultipart.getCount(); i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i); // Get each part of the multipart content
            if (bodyPart.isMimeType("text/plain")) {
                result.append(bodyPart.getContent()); // Append plain text
            } else if (bodyPart.isMimeType("text/html")) {
                result.append(bodyPart.getContent()); // Append HTML content
            } else if (bodyPart.getContent() instanceof MimeMultipart nestedMultipart) {
                result.append(getTextFromMimeMultipart(nestedMultipart)); // Handle nested multipart
            }
        }
        return result.toString(); // Return the extracted text
    }

    public void stopListening() {
        this.running = false; // Set the running flag to false, exiting the loop
    }
}
