alter table if exists users
    add column if not exists passport_file_id uuid,
    add column if not exists is_mail_confirm boolean not null default false;