alter table if exists chat_message
    drop column if exists id,
    add column if not exists id uuid primary key default gen_random_uuid(),
    alter column timestamp set not null,
    alter column timestamp set default current_timestamp,
    alter column sender_user_id set not null;
alter table if exists chat_message
    rename timestamp to date_time;

