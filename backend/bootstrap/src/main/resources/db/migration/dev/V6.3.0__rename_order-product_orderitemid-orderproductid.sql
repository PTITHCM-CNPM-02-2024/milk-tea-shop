ALTER TABLE milk_tea_shop_prod.OrderTopping
    DROP FOREIGN KEY OrderTopping_ibfk_1;

ALTER TABLE milk_tea_shop_prod.OrderProduct
    DROP COLUMN order_item_id;

ALTER TABLE milk_tea_shop_prod.OrderProduct
    DROP CONSTRAINT OrderDetail_pk;

ALTER TABLE milk_tea_shop_prod.OrderProduct
    ADD order_product_id INT NOT NULL COMMENT 'Mã chi tiết đơn hàng';

ALTER TABLE milk_tea_shop_prod.OrderProduct
    ADD PRIMARY KEY (order_product_id);

ALTER TABLE milk_tea_shop_prod.OrderProduct
    MODIFY order_product_id INT AUTO_INCREMENT NOT NULL COMMENT 'Mã chi tiết đơn hàng' FIRST;

ALTER TABLE milk_tea_shop_prod.OrderProduct
    ADD CONSTRAINT OrderProduct_pk UNIQUE (order_id, product_price_id);

ALTER TABLE milk_tea_shop_prod.OrderTopping
    ADD CONSTRAINT OrderTopping_ibfk_1 FOREIGN KEY (order_id) REFERENCES milk_tea_shop_prod.OrderProduct (order_product_id) ON DELETE NO ACTION;

ALTER TABLE milk_tea_shop_prod.OrderTopping
    MODIFY order_topping_id INT AUTO_INCREMENT;

