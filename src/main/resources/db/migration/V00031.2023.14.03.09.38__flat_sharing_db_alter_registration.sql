alter table registration
    alter column password type text,
    add column if not exists date_of_birth date,
    add column if not exists sciener_user_id int,
    add constraint fk_sciener_user_id foreign key(sciener_user_id) references sciener_user(id),
    add constraint users_sciener_user_id unique (sciener_user_id);

alter table registration
    rename column email to mail;
alter table registration
    rename column phone_number to phone;
alter table registration
    rename column photo_id to avatar_file_id;

update registration r
set date_of_birth = (select (current_date - interval '1 years' * rs.age) from registration rs where rs.id = r.id)
where date_of_birth is null ;

alter table registration drop column age;

alter index if exists registration_email_key rename to users_mail_key;
alter index if exists registration_phone_number_key rename to users_phone_key;
alter index if exists registration_pkey rename to users_pkey;

alter table registration rename to users