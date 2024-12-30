CREATE TABLE emails (
    id BIGSERIAL PRIMARY KEY,          -- Auto-incremented primary key
    from_address VARCHAR(255) NOT NULL, -- Sender's email address (required)
    to_address VARCHAR(255) NOT NULL,   -- Recipient's email address (required)
    subject VARCHAR(255),               -- Email subject (optional)
    body TEXT,                          -- Email body (long text)
    cc_address VARCHAR(255),            -- CC address (optional)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP 
    uid VARCHAR(255) UNIQUE  -- Timestamp for when the record is created
);
 