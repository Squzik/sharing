drop table if exists client;
drop sequence if exists registration_sequence, client_sequence;

alter table users
    add column if not exists nid uuid default gen_random_uuid() unique,
    add constraint users_avatar_file_id unique(avatar_file_id);

alter table booking
    add column if not exists user_id uuid references users(nid);
update booking
set user_id = subquery.nid
from (select u.id, u.nid from users u) as subquery
where client_id = subquery.id;
alter table booking
    drop column if exists client_id;

alter table chat_message
    add column if not exists sender_user_id uuid references users(nid);
update chat_message
set sender_user_id = subquery.nid
from (select u.id, u.nid from users u) as subquery
where sender_id = subquery.id;
alter table chat_message
    drop column if exists sender_id;

alter table chat_room
    add column if not exists first_user  uuid references users(nid),
    add column if not exists second_user uuid references users(nid),
    add constraint check_back_users_id check (first_user <> second_user and second_user <> first_user);
update chat_room
set first_user = subquery.nid
from (select u.id, u.nid from users u) as subquery
where first_client = subquery.id;
update chat_room
set second_user = subquery.nid
from (select u.id, u.nid from users u) as subquery
where second_client = subquery.id;
alter table chat_room
    drop column if exists first_client,
    drop column if exists second_client;

alter table flat
    add column if not exists user_id uuid references users(nid);
update flat
set user_id = subquery.nid
from (select u.id, u.nid from users u) as subquery
where client_id = subquery.id;
alter table flat
    drop column if exists client_id;

alter table flat_like
    add column user_id uuid references users(nid);
update flat_like
set user_id = subquery.nid
from (select u.id, u.nid from users u) as subquery
where client_id = subquery.id;
alter table flat_like
    drop column client_id;

alter table rating
    add column if not exists user_id uuid references users(nid);
update rating
set user_id = subquery.nid
from (select u.id, u.nid from users u) as subquery
where client_id = subquery.id;
alter table rating
    drop column if exists client_id;

alter table black_list
    add column if not exists user_id uuid references users(nid);
update black_list
set user_id = subquery.nid
from (select u.id, u.nid from users u) as subquery
where client_id = subquery.id;
alter table black_list
    drop column if exists client_id;

alter table users
    drop column id,
    add primary key (nid);
alter table users
    rename nid to id;