create table if not exists mail_confirmation(
    id uuid references users(id) primary key,
    code varchar(4) not null,    expire_datetime timestamp not null,
    number_of_attempts integer not null
)