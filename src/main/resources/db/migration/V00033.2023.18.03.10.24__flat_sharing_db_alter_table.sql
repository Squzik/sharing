alter table if exists client
    drop column if exists photo_id,
    add column if not exists photo_id uuid;

alter table users
    drop column if exists avatar_file_id,
    add column photo_id uuid;

alter table flat
    add column if not exists photos jsonb;

drop table if
exists photo;
