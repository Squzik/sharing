alter table booking
    add column if not exists status varchar(255),
    drop constraint if exists booking_date_range_excl;
alter table booking
    add constraint booking_date_range_excl exclude
        using gist(flat_id with =,int8range(booking_start, booking_end) with &&) where (booking.status <> 'Отклонен');