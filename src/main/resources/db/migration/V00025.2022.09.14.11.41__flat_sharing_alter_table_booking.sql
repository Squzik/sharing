create sequence if not exists booking_sequence;

alter table booking
    drop constraint if exists booking_pkey,
    add column if not exists id int4,
    alter column id set default nextval('booking_sequence'),
    add primary key (id);

update booking
set id = nextval('booking_sequence')
where id is null;

alter table booking
    drop constraint if exists booking_flat_id_tsrange_excl,
    alter column booking_start type int8 using EXTRACT(epoch from booking_start) * 1000,
    alter column booking_end type int8 using EXTRACT(epoch from booking_start) * 1000;

alter table booking
    add constraint booking_date_range_excl exclude
        using gist(flat_id with =,int8range(booking_start, booking_end) with &&);