package com.example.techpuram.service;

import jakarta.mail.*;
import jakarta.mail.event.MessageCountAdapter;
import jakarta.mail.event.MessageCountEvent;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMultipart;
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

    private volatile boolean running = true;

    public void connectAndListenForEmails() {
        try {
            // IMAP Server properties
            Properties properties = new Properties();
            properties.put("mail.imap.host", "imap.hostinger.com");
            properties.put("mail.imap.port", "993");
            properties.put("mail.imap.ssl.enable", "true");
            properties.put("mail.imap.auth", "true");

            // Create Session and Store
            Session session = Session.getInstance(properties);
            Store store = session.getStore("imap");
            store.connect("imap.hostinger.com", "harishkumar.d@techpuram.com", "n#U~5|9D");

            // Open the INBOX folder
            IMAPFolder inbox = (IMAPFolder) store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            // Add a listener for new messages
            inbox.addMessageCountListener(new MessageCountAdapter() {
                @Override
                public void messagesAdded(MessageCountEvent event) {
                    Message[] messages = event.getMessages();
                    for (Message message : messages) {
                        try {
                            processAndSaveEmail(inbox, message);
                        } catch (Exception e) {
                            logger.error("Error processing new email", e);
                        }
                    }
                }
            });

            // Listen for new messages in a loop
            while (running) {
                inbox.idle();
            }

            inbox.close(false);
            store.close();
        } catch (Exception e) {
            logger.error("Error while connecting and listening for emails", e);
        }
    }

    private void processAndSaveEmail(IMAPFolder inbox, Message message) throws MessagingException, IOException {
        long uid = inbox.getUID(message);

        // Skip if email UID already exists
        if (emailRepository.existsByUid(String.valueOf(uid))) {
            return;
        }

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

    public void stopListening() {
        this.running = false;
    }
}
