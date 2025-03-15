CREATE TABLE milk_tea_shop_prod.OrderDiscount
(
    order_discount_id INT UNSIGNED AUTO_INCREMENT NOT NULL COMMENT 'Mã giảm giá đơn hàng',
    order_product_id  INT UNSIGNED                NOT NULL COMMENT 'Mã chi tiết đơn hàng (nếu giảm giá áp dụng cho sản phẩm cụ thể)',
    discount_id       INT UNSIGNED                NOT NULL COMMENT 'Mã chương trình giảm giá được áp dụng',
    discount_amount   DECIMAL(10, 2)              NOT NULL COMMENT 'Số tiền giảm giá được áp dụng',
    created_at        datetime DEFAULT NOW()      NULL,
    updated_at        datetime DEFAULT NOW()      NULL,
    CONSTRAINT `PRIMARY` PRIMARY KEY (order_discount_id)
);

ALTER TABLE milk_tea_shop_prod.OrderDiscount
    ADD CONSTRAINT OrderDiscount_pk UNIQUE (order_product_id, discount_id);

ALTER TABLE milk_tea_shop_prod.OrderDiscount
    ADD CONSTRAINT OrderDiscount_ibfk_2 FOREIGN KEY (discount_id) REFERENCES milk_tea_shop_prod.Discount (discount_id) ON DELETE NO ACTION;

CREATE INDEX OrderDiscount_ibfk_2 ON milk_tea_shop_prod.OrderDiscount (discount_id);

ALTER TABLE milk_tea_shop_prod.OrderDiscount
    ADD CONSTRAINT OrderDiscount_ibfk_3 FOREIGN KEY (order_product_id) REFERENCES milk_tea_shop_prod.OrderProduct (order_product_id) ON DELETE NO ACTION;