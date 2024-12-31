package com.example.techpuram.service;

import jakarta.annotation.PostConstruct; // Importing the @PostConstruct annotation for lifecycle callback methods
import org.springframework.beans.factory.annotation.Autowired; // Importing the @Autowired annotation for dependency injection
import org.springframework.stereotype.Component; // Importing the @Component annotation to mark this class as a Spring-managed component

@Component
public class EmailFetcher { 
    // Marking this class as a Spring component so that Spring automatically detects and manages it as a bean.

    @Autowired
    private EmailServices emailServices;
    // Using @Autowired to inject the `EmailServices` dependency. 
    // This ensures that the `EmailFetcher` can call methods from `EmailServices` to listen for emails.

    @PostConstruct
    public void startEmailListener() {
        // The @PostConstruct annotation indicates that this method should be executed after the bean is fully initialized.
        // This is useful for starting tasks or setting up resources during application startup.

        new Thread(() -> emailServices.connectAndListenForEmails()).start();
        // Creating a new thread to call the `connectAndListenForEmails()` method from `EmailServices`.
        // This ensures that the email listening process runs in the background without blocking the main application thread.
        // The lambda expression `() -> emailServices.connectAndListenForEmails()` defines the task to be executed in the new thread.
    }
}
