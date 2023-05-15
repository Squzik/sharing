ALTER TABLE black_list
    RENAME COLUMN id_client TO client_id;

ALTER TABLE client
    RENAME COLUMN id_registration TO registration_id;
ALTER TABLE client
    RENAME COLUMN id_flat TO flat_id;

ALTER TABLE flat
    RENAME COLUMN id_client TO client_id;
ALTER TABLE flat
    RENAME COLUMN id_address TO address_id;
ALTER TABLE flat
    RENAME COLUMN id_lock TO lock_id;

ALTER TABLE rating
    RENAME COLUMN id_flat TO flat_id;
ALTER TABLE rating
    RENAME COLUMN id_client TO client_id;
ALTER TABLE rating
    RENAME COLUMN description TO review;

ALTER TABLE registration
    RENAME COLUMN id_role TO role_id;
ALTER TABLE registration
    RENAME COLUMN client_age TO age;
ALTER TABLE registration
    RENAME COLUMN client_name to name;
ALTER TABLE registration
    ADD COLUMN IF NOT EXISTS surname varchar(255);
ALTER TABLE registration
    ADD COLUMN IF NOT EXISTS patronymic varchar(255);

DROP TABLE IF EXISTS test;