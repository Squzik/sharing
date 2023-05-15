alter table sciener_code
    alter column password set not null,
    alter column sciener_lock_id set not null,
    add constraint sciener_lock_id_fkey foreign key (sciener_lock_id) references sciener_lock (id);