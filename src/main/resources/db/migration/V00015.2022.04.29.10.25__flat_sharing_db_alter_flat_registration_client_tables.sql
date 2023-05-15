ALTER TABLE flat
    DROP COLUMN IF EXISTS id_photo;

ALTER TABLE registration
    RENAME COLUMN photo to photo_id;

ALTER TABLE registration
    ALTER COLUMN photo_id TYPE INT8;

ALTER TABLE client
    ADD COLUMN IF NOT EXISTS photo_id INT8;
