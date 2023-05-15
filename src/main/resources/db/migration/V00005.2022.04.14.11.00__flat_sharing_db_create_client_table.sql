CREATE TABLE IF NOT EXISTS client
(
    id              INT8 PRIMARY KEY not null,
    id_registration INT8,
    id_rating       INTEGER,
    id_flat         INT8
);

create sequence client_sequence;