CREATE TABLE email_templates (
    templateId BIGINT AUTO_INCREMENT PRIMARY KEY,
    templateName VARCHAR(255) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    body VARCHAR(2000),
    properties TEXT -- Use TEXT to store larger string values
);
