package com.example.techpuram.email;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.techpuram.service.EmailFetchService;

@SpringBootApplication
public class EmailApplication implements CommandLineRunner {

    @Autowired
    private EmailFetchService emailFetchService;

    public static void main(String[] args) {
        SpringApplication.run(EmailApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        emailFetchService.fetchAndSaveEmails();  // Fetch and save emails when the app starts
    }
}
