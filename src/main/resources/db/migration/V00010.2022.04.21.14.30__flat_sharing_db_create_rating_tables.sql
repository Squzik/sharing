CREATE TABLE IF NOT EXISTS rating
(
    id              INT8 PRIMARY KEY not null,
    score           double precision,
    description     VARCHAR(400)
    );
create sequence rating_sequence;