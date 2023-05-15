CREATE TABLE IF NOT EXISTS black_list
(
    id        INT8 PRIMARY KEY not null,
    id_client INT8
);

create sequence black_list_sequence;
