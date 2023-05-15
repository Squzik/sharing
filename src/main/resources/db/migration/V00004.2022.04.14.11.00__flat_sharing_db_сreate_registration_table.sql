CREATE TABLE IF NOT EXISTS registration
(
    id           INT8 PRIMARY KEY not null,
    id_role      INTEGER,
    client_name  VARCHAR(255),
    client_age   INTEGER,
    phone_number VARCHAR(255),
    email        VARCHAR(255),
    password     VARCHAR(255),
    photo        INTEGER
);

create sequence registration_sequence;