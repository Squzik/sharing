ALTER TABLE registration
    DROP COLUMN IF EXISTS role_id;
ALTER TABLE rating
    DROP COLUMN IF EXISTS rating_type