package com.example.techpuram.service;

import jakarta.mail.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.techpuram.Entity.Email;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service
public class EmailFetchService {

    @Autowired
    private EmailServices emailServices;

    // Method to fetch emails from the IMAP server and save them to the database
    public void fetchAndSaveEmails() throws Exception {
        // Setup session and store
        Session session = createEmailSession();
        Store store = session.getStore("imap");
        store.connect("imap.hostinger.com", "harishkumar.d@techpuram.com", "n#U~5|9D");

        // Open folder and fetch messages
        Folder folder = store.getFolder("INBOX");
        folder.open(Folder.READ_ONLY);
        Message[] messages = folder.getMessages();

        // Loop through messages
        for (Message message : messages) {
            Email email = processMessage(message); // Use helper function for processing
            emailServices.saveEmail(email);
        }

        folder.close(false);
        store.close();
    }

    private Session createEmailSession() {
        Properties properties = new Properties();
        properties.put("mail.imap.host", "imap.hostinger.com");
        properties.put("mail.imap.port", "993");
        properties.put("mail.imap.ssl.enable", "true");
        return Session.getInstance(properties);
    }

    private Email processMessage(Message message) throws MessagingException, IOException {
        Email email = new Email();
        email.setFrom(message.getFrom()[0].toString());
        email.setRecipients(extractRecipients(message));
        email.setCc(extractCc(message));
        email.setSubject(message.getSubject());
        email.setBody(extractBody(message));
        return email;
    }

    private List<String> extractRecipients(Message message) throws MessagingException {
        List<String> recipientsList = new ArrayList<>();
        for (Address recipient : message.getAllRecipients()) {
            recipientsList.add(recipient.toString());
        }
        return recipientsList;
    }

    private List<String> extractCc(Message message) throws MessagingException {
        List<String> ccList = new ArrayList<>();
        if (message.getRecipients(Message.RecipientType.CC) != null) {
            for (Address cc : message.getRecipients(Message.RecipientType.CC)) {
                ccList.add(cc.toString());
            }
        }
        return ccList;
    }

    private String extractBody(Message message) throws IOException, MessagingException {
        String body = "";
        if (message.getContent() instanceof String string) {
            body = string;
        } else {
            body = "Non-text content received";
        }
        return body;
    }
}
