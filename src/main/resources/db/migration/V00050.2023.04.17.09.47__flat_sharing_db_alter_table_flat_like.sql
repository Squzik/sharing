alter table flat_like rename to favourites;

alter table if exists favourites
    drop column flat_id,
    drop constraint flat_like_user_id_fkey,
    add column if not exists flat_id uuid references flat(id),
    add constraint user_id_fkey foreign key (user_id) references users(id),
    add constraint favourites_pk primary key (user_id, flat_id);

drop sequence flat_like_sequence;
