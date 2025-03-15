ALTER TABLE milk_tea_shop_prod.ProductPrice
    ADD CONSTRAINT ProductPrice_pk UNIQUE (product_id, size_id);