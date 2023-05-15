ALTER TABLE address
DROP
CONSTRAINT IF EXISTS address_street_house_num_flat_key;
ALTER TABLE address
    ADD CONSTRAINT unique_address UNIQUE (region, city, street, house, num_flat);