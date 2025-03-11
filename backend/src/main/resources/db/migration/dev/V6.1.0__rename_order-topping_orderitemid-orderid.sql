ALTER TABLE OrderTopping
    CHANGE order_item_id order_id INT;

ALTER TABLE OrderTopping
    DROP CONSTRAINT OrderDetailTopping_pk;

ALTER TABLE milk_tea_shop_prod.OrderTopping
    ADD CONSTRAINT OrderTopping_pk UNIQUE (order_id, topping_id);