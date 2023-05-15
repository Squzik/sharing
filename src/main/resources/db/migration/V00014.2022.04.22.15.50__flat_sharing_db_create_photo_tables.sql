CREATE TABLE IF NOT EXISTS photo
(
    id      INT8 PRIMARY KEY not null,
    name    varchar(40),
    flat_id INT8,
    bytes   bytea
);

create sequence photo_sequence;