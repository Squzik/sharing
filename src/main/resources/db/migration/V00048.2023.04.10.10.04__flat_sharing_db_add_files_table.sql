create table if not exists files
(
    id         uuid primary key,
    booking_id uuid
        constraint files_booking_id_fkey references booking,
    users_id   uuid
        constraint files_users_id_fkey references users,
    flat_id    bigint
        constraint files_flat_id_fkey references flat,
    bucket     varchar(15) not null,
    status     varchar(30) not null
);

alter table if exists users
    drop column avatar_file_id;

alter table if exists flat
    drop column photos;

alter table if exists booking
    drop column contracts_files_ids,
    drop column contract_file_id;