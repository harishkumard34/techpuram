package com.example.techpuram.Entity.dto;

public class EmailDTO {
    private String fromAddress;
    private String toAddress;
    private String subject;
    private String body;
    private String ccAddress;
    private String bccAddress; 
    private Long templateId;  // Add templateId

    // Getter and Setter for 'fromAddress'
    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    // Getter and Setter for 'toAddress'
    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
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

    // Getter and Setter for 'ccAddress'
    public String getCcAddress() {
        return ccAddress;
    }

    public void setCcAddress(String ccAddress) {
        this.ccAddress = ccAddress;
    }

   
    public String getBccAddress() {
        return bccAddress;
    }

    public void setBccAddress(String bccAddress) {
        this.bccAddress = bccAddress;

    }    

    // Getter and Setter for 'templateId'
    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }
}
