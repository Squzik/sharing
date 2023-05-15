alter table if exists black_list
    drop column if exists id,
    add column if not exists id             uuid default gen_random_uuid() primary key,
    add column if not exists owner_user_id  uuid not null references users (id),
    add column if not exists date_of_create date not null default current_date,
    add column if not exists commentary     varchar,
    add constraint black_list_users_id_unique unique (owner_user_id, user_id);