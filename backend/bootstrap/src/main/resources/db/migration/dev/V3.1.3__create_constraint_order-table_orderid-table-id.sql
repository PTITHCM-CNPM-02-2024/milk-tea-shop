ALTER TABLE milk_tea_shop_prod.OrderTable
    ADD CONSTRAINT OrderTable_pk UNIQUE (order_id, table_id);