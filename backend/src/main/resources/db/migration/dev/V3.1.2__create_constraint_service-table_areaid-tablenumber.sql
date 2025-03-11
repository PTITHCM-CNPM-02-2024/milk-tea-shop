ALTER TABLE milk_tea_shop_prod.ServiceTable
    ADD CONSTRAINT ServiceTable_pk UNIQUE (area_id, table_number);