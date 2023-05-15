CREATE TABLE IF NOT EXISTS booking_status
(
    id          uuid default gen_random_uuid() PRIMARY KEY not null,
    title       VARCHAR(255) not null,
    description VARCHAR(400)
);