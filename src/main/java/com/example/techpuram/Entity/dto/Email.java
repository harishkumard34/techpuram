package com.example.techpuram.Entity.dto;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "emails")
public class Email {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fromAddress;
    private String toAddress;
    private String subject;

    @Column(length = 2000)
    private String body;

    private String ccAddress;

    @Column(unique = true) // Ensure UIDs are unique
    private String uid;


}
