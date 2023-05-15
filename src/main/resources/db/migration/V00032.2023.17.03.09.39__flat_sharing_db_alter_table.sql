alter table type_of_building
    add constraint type_of_building_title unique(title),
    drop constraint if exists code;

alter table type_of_flat
    drop column if exists id,
    add column if not exists id uuid primary key default gen_random_uuid(),
    add constraint type_of_flat_title unique(title),
    drop constraint if exists code;

alter table flat
    drop column if exists type_of_flat_id,
    add column if not exists type_of_flat_id uuid,
    add constraint fk_type_of_flat_id foreign key(type_of_flat_id) references type_of_flat(id);

drop sequence type_of_flat_sequence;

alter table type_of_rental
    add constraint type_of_rental_title unique(title),
    drop constraint if exists code;

alter table flat_status
    add constraint flat_status_title unique(title);