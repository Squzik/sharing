ALTER TABLE flat
    RENAME COLUMN number_of_beds to number_of_single_beds;
ALTER TABLE flat
    ADD COLUMN IF NOT EXISTS number_of_double_beds INTEGER;