CREATE TABLE IF NOT EXISTS flat
(
    id              INT8 PRIMARY KEY not null,
    id_client       INT8,
    id_photo        INT8,
    id_address      INT8,
    description     VARCHAR(400),
    status          VARCHAR(255),
    id_lock         INTEGER,
    price           INTEGER,
    number_of_rooms INTEGER,
    number_of_beds  INTEGER,
    id_rating_flat  INTEGER
    );
create sequence flat_sequence;
