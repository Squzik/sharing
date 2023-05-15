alter table if exists reviews
    drop constraint flat_id_fkey;

alter table if exists files
    drop constraint files_flat_id_fkey;

alter table if exists flat
    rename lock_id to sciener_lock_id;

alter table if exists flat
    rename status to is_hidden;

alter table if exists flat
    drop column id,
    drop column price,
    drop column number_of_single_beds,
    drop column number_of_double_beds,
    add column if not exists id uuid default gen_random_uuid() primary key,
    add column if not exists price double precision not null,
    add column if not exists floor integer not null,
    add column if not exists number_of_rooms integer not null,
    add column if not exists is_combined_bathroom boolean not null,
    add column if not exists furniture boolean not null,
    add column if not exists balcony boolean not null,
    add column if not exists appliances boolean not null,
    add column if not exists internet_cable_tv boolean not null,
    add constraint type_of_rental_id_fkey foreign key (type_of_rental_id) references type_of_rental(id),
    add constraint address_id_fkey foreign key (address_id) references address(id),
    add constraint sciener_lock_id_fkey foreign key (sciener_lock_id) references sciener_lock(id);

drop sequence flat_sequence;

alter table if exists booking
    drop column flat_id,
    add column if not exists flat_id uuid not null references flat(id);

alter table if exists flat_like
    drop column flat_id,
    add column if not exists flat_id uuid not null;

alter table if exists reviews
    drop column flat_id,
    add column if not exists flat_id uuid references flat(id);

alter table if exists files
    drop column flat_id,
    add column if not exists flat_id uuid references flat(id);

