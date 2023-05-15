alter table if exists chat_room
    drop column if exists id,
    add column if not exists id uuid primary key default gen_random_uuid(),
    alter column first_user set not null,
    alter column second_user set not null;
alter table if exists chat_message
    drop column if exists chat_room_id,
    add column if not exists chat_room_id uuid not null references chat_room(id),
    alter column message set not null;