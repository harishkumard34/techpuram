CREATE TABLE email_templates (
    template_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    template_name VARCHAR(255) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    body VARCHAR(2000),
    properties TEXT -- Use TEXT to store larger string values
);

