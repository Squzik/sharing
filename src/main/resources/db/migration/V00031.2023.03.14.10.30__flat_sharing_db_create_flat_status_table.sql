CREATE TABLE IF NOT EXISTS flat_status
(
    id          uuid default gen_random_uuid() PRIMARY KEY not null,
    title       VARCHAR(255),
    description VARCHAR(400)
);

alter table flat
    add column if not exists flat_status_id uuid,
    add constraint fk_flat_status
        foreign key (flat_status_id)
            references flat_status(id)