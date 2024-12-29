package com.example.techpuram.service;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component

public class EmailFetcher {
    @Autowired
    private EmailServices emailServices;

    @PostConstruct
    public void fetchEmailsOnStartup() {
        emailServices.fetchAndSaveEmails();
    }

}
