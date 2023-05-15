drop sequence if exists rating_sequence;

alter table rating rename to reviews;
alter table reviews
    rename review to commentary;

alter table reviews
    drop column if exists id,
    alter column score set not null,
    add column if not exists id uuid primary key default gen_random_uuid(),
    add column if not exists reviewer_user_id uuid not null,
    add column  if not exists date_time timestamp not null default current_timestamp,
    add constraint reviewer_user_id_fkey foreign key(reviewer_user_id) references users(id),
    add constraint flat_id_fkey foreign key(flat_id) references flat(id),
    add constraint target_id_not_null check (
        user_id is null or flat_id is null and
        (user_id is not null or flat_id is not null));

alter index if exists rating_pkey rename to review_pkey;
alter index if exists rating_user_id_fkey rename to review_user_id_fkey;
alter index if exists rating_pkey rename to review_pkey;