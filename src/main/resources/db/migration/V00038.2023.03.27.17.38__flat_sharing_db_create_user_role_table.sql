create table if not exists user_role
(
    id   uuid default gen_random_uuid() primary key,
    name varchar(20) not null
);

alter table users
    add column user_role_id uuid references user_role(id);