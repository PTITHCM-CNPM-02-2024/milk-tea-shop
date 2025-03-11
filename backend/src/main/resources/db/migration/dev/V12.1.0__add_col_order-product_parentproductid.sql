ALTER TABLE milk_tea_shop_prod.OrderProduct
    ADD parent_order_product_id INT UNSIGNED NULL COMMENT 'Mã đặt hàng sản phẩm gốc, khi sản phẩm ở hàng này được đặt là Topping';

ALTER TABLE milk_tea_shop_prod.OrderProduct
    ADD CONSTRAINT OrderProduct_ibfk_3 FOREIGN KEY (parent_order_product_id) REFERENCES milk_tea_shop_prod.OrderProduct (order_product_id) ON DELETE NO ACTION;

CREATE INDEX OrderProduct_ibfk_3 ON milk_tea_shop_prod.OrderProduct (parent_order_product_id);