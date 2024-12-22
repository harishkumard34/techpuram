CREATE TABLE emails (
    id SERIAL PRIMARY KEY,
    sender VARCHAR(255),
    recipients TEXT,
    cc TEXT,
    subject VARCHAR(255),
    body TEXT,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
