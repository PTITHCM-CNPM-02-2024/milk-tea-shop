ALTER TABLE milk_tea_shop_prod.OrderDetail
    ADD CONSTRAINT OrderDetail_pk UNIQUE (product_price_id, order_id);