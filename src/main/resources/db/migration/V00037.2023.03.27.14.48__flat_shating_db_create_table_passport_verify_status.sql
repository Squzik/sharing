create table if not exists passport_verify_status
(
    id          uuid default gen_random_uuid() primary key,
    title       varchar(50) not null,
    description varchar(255)
);

alter table users
    add column if not exists passport_verify_status_id uuid references passport_verify_status(id);
