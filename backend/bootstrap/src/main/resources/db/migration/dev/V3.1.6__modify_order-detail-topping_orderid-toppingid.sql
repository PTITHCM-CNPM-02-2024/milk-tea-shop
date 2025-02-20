ALTER TABLE milk_tea_shop_prod.OrderDetailTopping
    ADD CONSTRAINT OrderDetailTopping_pk UNIQUE (order_item_id, topping_id);