alter table sciener_lock
    rename exists to is_linked;
alter table sciener_lock
    add constraint fk_sciener_user_id foreign key(sciener_user_id) references sciener_user(id),
    alter column sciener_user_id set not null,
    alter column lock_name set not null,
    alter column is_linked set default true,
    alter column is_linked set not null;