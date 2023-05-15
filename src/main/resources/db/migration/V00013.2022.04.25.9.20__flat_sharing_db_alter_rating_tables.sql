drop table rating_flat;

alter table rating
    add column rating_type VARCHAR(255);

alter table flat
    drop column id_rating_flat;

alter table flat
    add column id_rating INT8;

alter table rating
    add column id_flat INT8;

alter table rating
    add column id_client INT8;

drop sequence rating_flat_sequence;

alter table client
    drop column id_rating;

alter table flat
    drop column id_rating;