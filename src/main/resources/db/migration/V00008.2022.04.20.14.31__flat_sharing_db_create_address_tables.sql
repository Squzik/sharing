CREATE TABLE IF NOT EXISTS address
(
    id       INT8 PRIMARY KEY not null,
    region   VARCHAR(50),
    city     VARCHAR(50),
    street   VARCHAR(50),
    house    VARCHAR(50),
    num_flat integer,
    lng      double precision,
    lat      double precision,
    UNIQUE (street, house, num_flat)
);
create sequence address_sequence;
