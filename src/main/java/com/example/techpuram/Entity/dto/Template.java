package com.example.techpuram.Entity.dto;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "email_templates")

public class Template {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long templateId;

    private String templateName;

    private String subject;

    @Column(length = 64000)
    private String body;

    private String properties; // You can use a JSON format or String-based properties


}
