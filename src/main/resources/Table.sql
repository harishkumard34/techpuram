CREATE TABLE emails (
    id BIGSERIAL PRIMARY KEY, -- Auto-incremented primary key
    from_address VARCHAR(255), -- Default column size for Strings
    to_address VARCHAR(255), -- Default column size for Strings
    subject VARCHAR(255), -- Default column size for Strings
    body TEXT, -- Specified as length 2000 in JPA; mapped to TEXT in most databases
    cc_address VARCHAR(255) -- Default column size for Strings
);