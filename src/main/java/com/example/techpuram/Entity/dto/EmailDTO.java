package com.example.techpuram.Entity.dto;

public class EmailDTO {
    private String from;
    private String to;
    private String subject;
    private String body;
    private String cc;  // Change here: cc is now a String

    // Getter and Setter for 'from'
    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    // Getter and Setter for 'to'
    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    // Getter and Setter for 'subject'
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    // Getter and Setter for 'body'
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    // Getter and Setter for 'cc' (now as a single String)
    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }
}
