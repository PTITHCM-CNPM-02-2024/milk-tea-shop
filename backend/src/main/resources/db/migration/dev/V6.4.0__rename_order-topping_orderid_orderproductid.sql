ALTER TABLE OrderTopping
    CHANGE order_id order_product_id INT;

ALTER TABLE OrderTopping
    DROP CONSTRAINT OrderTopping_pk;

ALTER TABLE milk_tea_shop_prod.OrderTopping
    ADD CONSTRAINT OrderTopping_pk UNIQUE (order_product_id, topping_id);