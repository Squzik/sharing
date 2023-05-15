alter table booking
    rename code_id to sciener_code_id;

drop table booking_status;

alter table booking
    drop constraint if exists booking_date_range_excl,
    drop column if exists id,
    drop column if exists booking_start,
    drop column if exists booking_end,
    add column if not exists id uuid primary key default gen_random_uuid(),
    add column if not exists start_date_and_time timestamp not null,
    add column if not exists end_date_and_time timestamp not null,
    add column if not exists date_of_booking_request date not null default current_date,
    add column if not exists booking_status varchar not null,
    add column if not exists number_of_people int not null,
    add column if not exists contract_file_id uuid,
    add constraint sciener_code_id_fkey foreign key(sciener_code_id) references sciener_code(id);

drop sequence if exists booking_sequence;