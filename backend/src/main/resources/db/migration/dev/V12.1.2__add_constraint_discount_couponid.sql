ALTER TABLE milk_tea_shop_prod.Discount
    ADD CONSTRAINT Discount_ibfk_2 FOREIGN KEY (coupon_id) REFERENCES milk_tea_shop_prod.Coupon (coupon_id) ON DELETE NO ACTION;

CREATE INDEX Discount_ibfk_2 ON milk_tea_shop_prod.Discount (coupon_id);