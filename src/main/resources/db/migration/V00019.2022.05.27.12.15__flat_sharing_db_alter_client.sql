ALTER TABLE client
    DROP COLUMN  IF EXISTS flat_id;
ALTER TABLE client
    DROP COLUMN IF EXISTS registration_id;