alter table flat
drop
column id_rating_flat;

alter table flat
    add column id_rating_flat INT8;

alter table client
    drop
        column id_rating;

alter table client
    add column id_rating INT8;
