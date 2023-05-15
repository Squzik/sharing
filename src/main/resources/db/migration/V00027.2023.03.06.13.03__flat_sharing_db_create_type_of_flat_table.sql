alter table flat
    add column if not exists type_of_flat_id INT4;

CREATE TABLE IF NOT EXISTS type_of_flat
(
    id          INT4 PRIMARY KEY not null,
    title       VARCHAR(255),
    code        INTEGER not null,
    description VARCHAR(400)
);

create sequence type_of_flat_sequence;