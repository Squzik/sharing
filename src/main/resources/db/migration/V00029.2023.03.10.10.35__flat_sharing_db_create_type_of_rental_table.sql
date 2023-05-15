create extension if not exists "uuid-ossp";

alter table flat
    add column if not exists type_of_rental_id uuid;

CREATE TABLE IF NOT EXISTS type_of_rental
(
    id          uuid default gen_random_uuid() primary key not null,
    title       VARCHAR(255),
    code        INTEGER not null,
    description VARCHAR(400)
);