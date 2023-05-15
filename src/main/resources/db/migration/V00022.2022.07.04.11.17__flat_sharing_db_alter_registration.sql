ALTER TABLE registration
    ADD UNIQUE (email);
ALTER TABLE registration
    ADD UNIQUE (phone_number);