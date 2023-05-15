alter table if exists mail_confirmation
    drop column if exists code,
    add column if not exists code integer not null,
    alter column number_of_attempts set default 0;