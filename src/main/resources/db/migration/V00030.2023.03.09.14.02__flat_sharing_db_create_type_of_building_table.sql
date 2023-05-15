CREATE TABLE IF NOT EXISTS type_of_building
(
    id          uuid default gen_random_uuid() PRIMARY KEY not null,
    title       VARCHAR(255),
    code        INTEGER not null,
    description VARCHAR(400)
);

alter table flat
    add column if not exists type_of_building_id uuid,
    add constraint fk_type_of_building
        foreign key (type_of_building_id)
            references type_of_building(id)