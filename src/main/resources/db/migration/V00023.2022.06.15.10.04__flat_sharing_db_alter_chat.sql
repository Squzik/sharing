CREATE TABLE IF NOT EXISTS chat_message
(
    id int8 PRIMARY KEY not null,
    chat_room_id int8,
    sender_id int8,
    message VARCHAR(400),
    timestamp timestamp not null
);

create sequence chat_message_sequence;

CREATE TABLE IF NOT EXISTS chat_room
(
    id int8 PRIMARY KEY not null,
    first_client int8,
    second_client int8,
    unique(first_client, second_client)
);

create sequence chat_room_sequence;