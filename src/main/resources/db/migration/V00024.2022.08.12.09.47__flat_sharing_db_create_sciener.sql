create table if not exists sciener_user
(
    id             int4,
    username       varchar(40),
    access_token   varchar(40),
    refresh_token  varchar(40),
    token_end_time int8,
    primary key (id)
);

create table if not exists sciener_lock
(
    id              int4,
    lock_name       varchar(40),
    sciener_user_id int4,
    exists          bool,
    primary key (id)
);

create table if not exists sciener_code
(
    id              int4,
    password        varchar(10),
    sciener_lock_id int4,
    primary key (id)
);

alter table client
    add column if not exists sciener_user_id int4;

alter table booking
    add column if not exists code_id int4;

alter table flat
    drop column if exists status;

alter table flat
    add column if not exists status bool;