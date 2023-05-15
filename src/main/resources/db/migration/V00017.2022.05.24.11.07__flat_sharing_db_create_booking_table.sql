CREATE EXTENSION IF NOT EXISTS btree_gist;

CREATE TABLE IF NOT EXISTS booking
(
    client_id     INT8,
    flat_id       INT8,
    PRIMARY KEY (flat_id, client_id),
    booking_start TIMESTAMP NOT NULL,
    booking_end   TIMESTAMP NOT NULL,
    EXCLUDE USING gist (flat_id WITH =, tsrange(booking_start, booking_end) WITH &&)
);