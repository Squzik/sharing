CREATE TABLE IF NOT EXISTS rating_flat
(
    id              INT8 PRIMARY KEY not null,
    id_photo        INT8,
    score           double precision,
    description     VARCHAR(400)
    );
create sequence rating_flat_sequence;