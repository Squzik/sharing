alter table if exists booking
    add column if not exists contracts_files_ids jsonb;
